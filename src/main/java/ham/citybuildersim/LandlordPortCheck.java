package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.PortDivision;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The landlord port, held to FR3's brief (docs/queue/FR3.md): the city builds
 * and keeps the sea terminals and Freight's Ports division works them.
 *
 * WHY THIS HARNESS EXISTS. Until FR3 the four terminals (and the Bulk Berth) were
 * the city's end to end: no sector, their dockers paid with the transit bill,
 * the handling of a tonne nobody's. FR3 splits the two as a landlord port does:
 * the city keeps the berths and their repairs, Freight works them, pays the
 * crews, bills the shippers for the handling and pays the city dues. It was
 * written in slices by agents from the brief and is held here from the brief's
 * rules and the model's own getters and constants: no figure below is typed in
 * that a getter or a constant could give. Eleven claims, numbered as FR3.md
 * section 6 numbers them (the save is 9 here, so its advice is 10 and its real run, LongPlaytest's, 11):
 *
 *   1. THE BULK BERTH. (a placeholder: slice 1's, PortCheck holds it now)
 *   2. THE SHIPS. (a placeholder: slice 1's, PortCheck holds it now)
 *   3. THE CREWS. A city with a terminal: the transit bill carries its upkeep
 *      and not its crew; Freight's wage bill carries the crew at the same fill;
 *      the two together are what portsBill() was, to the cent; the city's posts
 *      are counted once, whoever pays them; the drivers' lay-off never touches
 *      a terminal's posts; the railway's and Haulage's payroll lines read what
 *      they read before the crews came, to the bit; and the crew by kind sums
 *      to the payroll moved.
 *   4. HANDLING, EACH LEG ONCE. A city landing and shipping dry bulk by sea over a Bulk Terminal: each
 *      shipper's "Port handling" line is its sea tonnes of each kind x the quote it was billed at, to the
 *      cent; Freight's "Port handling billed" is the shippers' lines summed; a sector that only buys at
 *      home and a kind with no berth pay none.
 *   5. THE DUES. Freight's "Port dues" are the tonnes x each kind's dues rate x the dial, to the cent, and
 *      the treasury's and the national accounts' lines equal them; twins but for the dial: none at 0, twice at 2.
 *   6. THE DUES' DEFAULT. duesRateOf() is the annuity worked here by hand (the kind's terminals at cash cost
 *      and materials at market, the city's own rate over PORT_LIFE_MONTHS, over the berths' month at
 *      DESIGN_USE), and a city owing more pays a dearer due.
 *   7. THE PRICE. The first crew quote is the target itself, the next walks REPRICE_SPEED of the way, the dues passed through at the month's rate; the
 *      scarcity is 1 to the berths' month and the cap at twice; a berth below DESIGN_USE bills less than
 *      its crew and its dues.
 *   8. THE MONEY. The money audit closes every month of a city working a port that pays dues.
 *   9. THE SAVE. The dial crosses the policy state (and a short array reads the default); the quotes, the
 *      month's dues and handling and the treasury's lines cross a save and a reload, to the bit.
 *  10. THE ADVICE. (a placeholder: slice 4's)
 *  11. A REAL RUN. (LongPlaytest's)
 *
 * It tests the model through its public getters and constants and changes
 * nothing in it; where the code and the brief disagree it says FAIL and the
 * line says where. Every fixture is a city of its own in GameFiles.scratch().
 */
public class LandlordPortCheck {

    static int fails = 0;

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { r.run(); } finally { System.setOut(real); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Bitwise, not near. See TradeCostCheck for why this harness family compares this way. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0 || (Double.isNaN(a) && Double.isNaN(b));
    }

    /** A cent, in the game's money: D$0.01, and the money is in D$ thousands. */
    static final double CENT = 0.01 / 1000;

    static boolean toTheCent(double a, double b) {
        return Math.abs(a - b) <= CENT;
    }

    /* ================================================================
       THE FIXTURES
       ================================================================ */

    /**
     * A city with a Freight sector that has a railway and a depot, and houses,
     * shops, roads, power and water around them, not yet played. The track
     * and the depot are handed to the city rather than waited for
     * (HaulageCheck.lay()); Freight is held from planning so the planner does
     * not change the premise (an assertion whose subject is decided by a
     * planner is an assertion about the planner). The terminals are laid by the
     * caller, since each section wants them before or after.
     */
    static Game city(String label) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getForeignAccounts().pinRate(1.0);
            g.getWorldEconomy().pin();
            g.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 900, true);
            g.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            g.getBusinessInvestment().holdSector(Sectors.FREIGHT);
            g.getSectors().freight().setCash(1_000_000);
            HaulageCheck.lay(g, "Rail Spur", 2);
            HaulageCheck.lay(g, "Truck Depot", 1);
        });
        return g;
    }

    /** The terminals a section stands: a General Cargo Terminal, and a Tanker Terminal besides. Handed to the city on its ground (HaulageCheck.lay()), without the coast the build menu asks for. */
    static void standTerminals(Game g, boolean tanker) {
        quietly(() -> {
            HaulageCheck.lay(g, "General Cargo Terminal", 1);
            if (tanker) HaulageCheck.lay(g, "Tanker Terminal", 1);
        });
    }

    /** A fill rate for each tier, none alike and none whole, so a figure struck at the wrong tier's fill misses. */
    static double[] fills() {
        double[] fill = new double[JobType.values().length];
        for (int i = 0; i < fill.length; i++) fill[i] = 0.55 + 0.035 * i;
        return fill;
    }

    /** The posts of the buildings of one template, per tier, from the template: quantity x jobs, nothing from the manager's sums. */
    static long[] postsOf(BuildingManager bm, String name) {
        BuildingsTemplate t = bm.getTemplateByName(name);
        long[] posts = new long[JobType.values().length];
        long n = bm.getQuantity(t.getId());
        for (JobType j : JobType.values()) posts[j.ordinal()] = n * t.getJobs(j);
        return posts;
    }

    static long sum(long[] a) {
        long t = 0;
        for (long v : a) t += v;
        return t;
    }

    /** What a month of these posts costs, at these wages and fill rates (full staffing when `fill` is null). */
    static double bill(long[] posts, double[] rates, double[] fill) {
        double total = 0;
        for (int i = 0; i < posts.length; i++) total += posts[i] * rates[i] * (fill == null ? 1 : fill[i]);
        return total;
    }

    /* ================================================================
       3. THE CREWS
       ================================================================ */

    static void crews() {
        System.out.println("\n--- 3. the crews: the city's berths, Freight's payroll ---");
        crewsOf(false);
        crewsOf(true);
        transitBill();
    }

    /** One General Cargo Terminal stood in a city that had none (and a Tanker Terminal besides, when `tanker`): what moves from the city's bill to Freight's payroll. */
    static void crewsOf(boolean tanker) {
        String what = tanker ? "a General Cargo and a Tanker Terminal" : "a General Cargo Terminal";
        System.out.println("\n--- ...with " + what + " ---");
        Game g = city(tanker ? "landlordport-crews-tanker" : "landlordport-crews");
        // Two months, so the economy has struck its wages (an unplayed city's are all nothing and every bill below would be too).
        quietly(() -> { for (int i = 0; i < 2; i++) g.toggleNextMonth(); });
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        BuildingManager bm = g.getBuildingManager();
        double[] rates = g.getEconomyManager().getWageRates();
        double[] fill = fills();
        int di = JobType.DIPLOMA.ordinal();
        BuildingsTemplate depot = bm.getTemplateByName("Truck Depot");

        // The depot keeps fewer trucks than its room, so the drivers are laid off in the Haulage division's own way.
        double trucks = Math.floor(depot.getTruckRoom() / 2) + 0.7;
        fr.setPantry(Good.TRUCKS, trucks);
        fr.updateJobFillRate(fill);
        h.strikeDrivers();
        fr.updateWages(rates, fr.postsPerTier());

        long[] cityBefore = bm.getTotalJobs();
        long[] everyBefore = new long[cityBefore.length];
        for (JobType j : JobType.values()) everyBefore[j.ordinal()] = bm.getTotalJobsAtEveryPost(j);
        double payBefore = fr.getPayroll(), fullBefore = fr.getPayrollAtFullStaffing();
        double railBefore = fr.railPayroll(), haulBefore = fr.haulagePayroll(), shareBefore = fr.haulagePayrollShare();
        double driversBefore = h.offeredShare(JobType.DIPLOMA);
        long[] ownedBefore = bm.getOwnedJobArrayBySector(fr.key());
        long[] offeredBefore = bm.getPostsOfferedBySector(fr.key());
        double ownedValueBefore = bm.getBuildingsValueBySector(fr.key()), ownedLandBefore = bm.getLandSqFtBySector(fr.key());
        long withheldBefore = bm.getPostsWithheld(JobType.DIPLOMA);
        double idle = Math.floor(depot.getTruckRoom() - trucks) * Haulage.DRIVERS_A_TRUCK;
        assertTrue("fixture: a depot standing with fewer trucks than its room (drivers laid off), and no terminal yet",
                idle > 0 && driversBefore < 1 && withheldBefore == (long) idle && bm.getCategoryPayroll(BuildingType.PORTS, rates, fill) == 0);
        assertTrue("...and with no terminal the Ports crews' payroll and its share are nothing",
                fr.portsPayroll() == 0 && fr.portsPayrollShare() == 0 && sum(bm.getCrewArrayBySector(fr.key())) == 0);

        standTerminals(g, tanker);
        h.strikeDrivers();
        fr.updateWages(rates, fr.postsPerTier());
        BuildingsTemplate general = bm.getTemplateByName("General Cargo Terminal");
        BuildingsTemplate tank = bm.getTemplateByName("Tanker Terminal");
        long[] crewPosts = postsOf(bm, "General Cargo Terminal");
        if (tanker) for (int i = 0; i < crewPosts.length; i++) crewPosts[i] += postsOf(bm, "Tanker Terminal")[i];
        double crewPay = bm.getCategoryPayroll(BuildingType.PORTS, rates, fill);
        assertTrue("fixture: the terminal(s) stand, with crews at a wage and a fill",
                bm.getQuantity(general.getId()) == 1 && (!tanker || bm.getQuantity(tank.getId()) == 1)
                        && sum(crewPosts) > 0 && crewPay > 0);

        // ---- (a) the transit bill's ports part is the berths' repairs ----
        report("(a) THE CITY KEEPS THE BERTHS' REPAIRS: portsBill() is the terminals' upkeep, and not their crew",
                same(g.portsBill(), bm.getUpkeepByCategory(BuildingType.PORTS)) && g.portsBill() < crewPay,
                String.format("D$%,.4fk of upkeep against D$%,.4fk of crew", g.portsBill(), crewPay));

        // ---- (b) Freight's wage bill grows by the crew, to the cent ----
        double payAfter = fr.getPayroll();
        report("(b) FREIGHT PAYS THE CREW: its wage bill grows by the terminals' posts x wage x fill, to the cent",
                toTheCent(payAfter - payBefore, crewPay) && toTheCent(fr.portsPayroll(), crewPay) && payAfter > payBefore,
                String.format("grew D$%,.4fk; the city paid D$%,.4fk for them", payAfter - payBefore, crewPay));
        report("...and the bill at full staffing grows by their posts at the wage of each tier",
                toTheCent(fr.getPayrollAtFullStaffing() - fullBefore, bill(crewPosts, rates, null)),
                String.format("grew D$%,.4fk", fr.getPayrollAtFullStaffing() - fullBefore));
        report("...the two together are what portsBill() was: the upkeep it keeps and the crew it lost",
                toTheCent(g.portsBill() + fr.portsPayroll(), bm.getCategoryPayroll(BuildingType.PORTS, rates, fill) + bm.getUpkeepByCategory(BuildingType.PORTS)),
                String.format("D$%,.4fk + D$%,.4fk", g.portsBill(), fr.portsPayroll()));
        double share = fr.portsPayrollShare();
        assertTrue("...the Ports division's share of the payroll is its crew over the whole, in (0, 1)",
                share > 0 && share < 1 && Math.abs(share - fr.portsPayroll() / fr.getPayroll()) <= 1e-12);

        // ---- the owner keeps everything but the posts ----
        assertTrue("THE BERTHS STAY THE CITY'S: Freight's buildings' value, ground and own posts are what they were, and its operated posts grew by the crew",
                same(bm.getBuildingsValueBySector(fr.key()), ownedValueBefore) && same(bm.getLandSqFtBySector(fr.key()), ownedLandBefore)
                        && java.util.Arrays.equals(bm.getOwnedJobArrayBySector(fr.key()), ownedBefore)
                        && sum(bm.getJobArrayBySector(fr.key())) == sum(ownedBefore) + sum(crewPosts)
                        && java.util.Arrays.equals(bm.getCrewArrayBySector(fr.key()), crewPosts));
        assertTrue("...and no sector owns a terminal: the building's owner is nobody's, its operator Freight's",
                !general.isOwnedBySector() && general.getOperator().equals(fr.key()) && general.getSector().isEmpty()
                        && bm.getTemplateByName("House").getOperator().equals(bm.getTemplateByName("House").getSector()));

        // ---- (c) the city counts the posts once, whoever pays them ----
        long[] cityAfter = bm.getTotalJobs();
        boolean once = true, byTier = true;
        for (JobType j : JobType.values()) {
            int i = j.ordinal();
            once &= bm.getTotalJobsAtEveryPost(j) - everyBefore[i] == crewPosts[i];
            byTier &= cityAfter[i] - cityBefore[i] == crewPosts[i];
        }
        report("(c) THE CITY'S POSTS ARE THE SUM OVER ITS BUILDINGS: the terminals add exactly their posts, counted once, in every tier",
                once && byTier, String.format("%,d posts before, %,d after (+%,d)", sum(cityBefore), sum(cityAfter), sum(crewPosts)));
        boolean everyPostHasAnEmployer = true;
        for (JobType j : JobType.values()) {
            long operated = bm.getJobArrayBySector("")[j.ordinal()];
            long offered = 0;
            for (Sector s : g.getSectors().all()) offered += bm.getPostsOfferedBySector(s.key())[j.ordinal()];
            everyPostHasAnEmployer &= operated + offered == bm.getTotalJobs(j);
        }
        assertTrue("...and every post has one payer: the city's own posts and the sectors' offered posts come to the city's total, in every tier",
                everyPostHasAnEmployer);

        // ---- the drivers' lay-off is struck on the posts Freight owns ----
        report("THE DRIVERS' LAY-OFF NEVER TOUCHES A TERMINAL'S POSTS: the drivers' share is what it was, and the DIPLOMA posts offered are the owned ones less the idle drivers, plus the crew",
                same(h.offeredShare(JobType.DIPLOMA), driversBefore)
                        && bm.getPostsOfferedBySector(fr.key())[di] == offeredBefore[di] + crewPosts[di]
                        && bm.getPostsOfferedBySector(fr.key())[di] == ownedBefore[di] - (long) idle + crewPosts[di],
                String.format("%d of %d DIPLOMA posts offered (%.0f drivers laid off, %d dockers)",
                        bm.getPostsOfferedBySector(fr.key())[di], bm.getJobArrayBySector(fr.key())[di], idle, crewPosts[di]));
        assertTrue("...the city withholds the same drivers it did, and no docker, and Freight's wage bill pays the posts offered",
                bm.getPostsWithheld(JobType.DIPLOMA) == withheldBefore
                        && bm.getTotalJobsAtEveryPost(JobType.DIPLOMA) - bm.getTotalJobs(JobType.DIPLOMA) == (long) idle
                        && toTheCent(fr.getPayrollAtFullStaffing(), bill(bm.getPostsOfferedBySector(fr.key()), rates, null)));

        // ---- (d) the railway's and Haulage's payroll lines read what they read before, to the bit ----
        report("(d) THE RAILWAY'S AND HAULAGE'S PAYROLL ARE WHAT THEY WERE, TO THE BIT: ownPayroll(), the share and each division's line",
                same(fr.ownPayroll(), payBefore) && same(fr.haulagePayrollShare(), shareBefore)
                        && same(fr.railPayroll(), railBefore) && same(fr.haulagePayroll(), haulBefore),
                String.format("rail D$%,.4fk, haulage D$%,.4fk of the D$%,.4fk the track and the depot pay", fr.railPayroll(), fr.haulagePayroll(), fr.ownPayroll()));
        assertTrue("...and the three together are the sector's whole payroll: rail + haulage + ports",
                toTheCent(fr.railPayroll() + fr.haulagePayroll() + fr.portsPayroll(), fr.getPayroll()));

        // ---- (e) the crew by kind sums to the payroll moved ----
        double[] byKind = fr.portsCrewByKind();
        Ports.Cargo[] kinds = Ports.Cargo.values();
        double total = 0;
        for (double v : byKind) total += v;
        double wantGeneral = bill(postsOf(bm, "General Cargo Terminal"), rates, fill);
        double wantLiquid = tanker ? bill(postsOf(bm, "Tanker Terminal"), rates, fill) : 0;
        boolean idleKinds = true;
        for (Ports.Cargo k : kinds) if (k != general.berthCargo() && k != tank.berthCargo()) idleKinds &= byKind[k.ordinal()] == 0;
        report("(e) THE CREW BY KIND SUMS TO THE PAYROLL MOVED: each kind's terminals' posts x wage x fill, and no crew on a kind with no berth",
                toTheCent(total, crewPay) && toTheCent(total, fr.portsPayroll())
                        && toTheCent(byKind[general.berthCargo().ordinal()], wantGeneral)
                        && toTheCent(byKind[tank.berthCargo().ordinal()], wantLiquid) && idleKinds,
                String.format("general D$%,.4fk, liquid D$%,.4fk of D$%,.4fk", byKind[general.berthCargo().ordinal()],
                        byKind[tank.berthCargo().ordinal()], crewPay));
    }

    /**
     * A played city with its terminals: the transit bill has the berths' repairs and no crew, and the
     * households are paid what the employers paid, the dockers in Freight's bill and nobody else's.
     */
    static void transitBill() {
        System.out.println("\n--- ...and a played month: the transit bill, and the households' pay ---");
        Game g = city("landlordport-month");
        standTerminals(g, true);
        quietly(() -> { for (int i = 0; i < 2; i++) g.toggleNextMonth(); });
        BuildingManager bm = g.getBuildingManager();
        PopulationManager people = g.getPopulationManager();
        Freight fr = g.getSectors().freight();
        double[] wages = people.getWagesPerType(), fill = people.getJobFillRate();
        double infra = bm.getCategoryPayroll(BuildingType.INFRASTRUCTURE, wages, fill) + bm.getUpkeepByCategory(BuildingType.INFRASTRUCTURE);
        double crew = bm.getCategoryPayroll(BuildingType.PORTS, wages, fill);
        assertTrue("fixture: two months played with the terminals standing, no transit line to pay, and crews at a wage",
                g.getMonth() >= 2 && crew > 0 && infra == 0);
        report("THE TRANSIT BILL'S PORTS PART IS THE BERTHS' UPKEEP ONLY: the month's bill is that and nothing for the crew",
                toTheCent(g.getEconomyManager().getTransitBill(), bm.getUpkeepByCategory(BuildingType.PORTS)) && g.getEconomyManager().getTransitBill() < crew,
                String.format("D$%,.4fk of a D$%,.4fk crew", g.getEconomyManager().getTransitBill(), crew));
        report("...and Freight's month pays the crew at the fill the city counted: its payroll is a crew's more than its own posts'",
                toTheCent(fr.portsPayroll(), crew) && fr.getPayroll() > fr.ownPayroll(),
                String.format("D$%,.4fk against D$%,.4fk", fr.portsPayroll(), crew));
        double employers = LabourCheck.everyOtherEmployer(g);
        for (Sector s : g.getSectors().all()) employers += s.getPayroll();
        report("THE HOUSEHOLDS ARE PAID WHAT THE EMPLOYERS PAID: the sectors' payrolls, the dockers in Freight's, and the city's own, once",
                Math.abs(employers / people.getTotalWage() - 1) <= 1e-9,
                String.format("employers D$%,.4fk, wages D$%,.4fk", employers, people.getTotalWage()));
    }

    /* ================================================================
       4-9. THE PORT DIVISION: THE HANDLING, THE DUES, THE PRICE, THE MONEY, THE SAVE
       ================================================================ */

    static final int K = Ports.Cargo.values().length;

    /**
     * A city with the Haulage fixture's bulk chain (three iron mines and a pelletising plant that lands its coal and
     * ore and ships its pellets, HaulageCheck.base()), played fourteen months, then given a Bulk Terminal and played
     * three more so the ore, the coal and the pellets go by sea in the berths' share. The fixture is the lorries' and
     * the ships' trade, with the berth the only new thing in it.
     */
    static Game seaBase(GameFiles files) {
        Game g = HaulageCheck.played(files, 14);
        quietly(() -> {
            HaulageCheck.lay(g, "Bulk Terminal", 1);
            for (int i = 0; i < 3; i++) g.toggleNextMonth();
        });
        return g;
    }

    /** The month about to be billed, worked out from the model's own getters before the haul runs: each shipper's sea tonnes by kind, and the quotes in force. */
    static final class Plan {
        final Map<Sector, double[]> sea = new LinkedHashMap<>();
        final Map<Sector, double[]> landed = new LinkedHashMap<>(), shipped = new LinkedHashMap<>();
        final double[] quote = new double[K];
        final boolean[] known = new boolean[K];
    }

    /**
     * Last month's rows, at the berths' share in force while they moved (Ports.seaShareOf() at the railway's carried
     * share): units in + units out, x the good's tonnes a unit, x that share, by the good's kind of cargo. The brief's
     * rule, not the loop's.
     */
    static Plan plan(Game g) {
        Plan p = new Plan();
        Freight fr = g.getSectors().freight();
        Ports ports = g.getPorts();
        for (Sector s : g.getSectors().all()) {
            if (s == fr) continue;
            for (Good good : Good.values()) {
                Traffic stream = good.traffic();
                if (stream == null || !stream.isFreight() || good.cargo() == null) continue;
                double ship = ports.seaShareOf(good, fr.getCarried()[stream.ordinal()]);
                double in = s.unitsImported(good), out = s.unitsExported(good);
                if (!(ship > 0) || in + out <= 0) continue;
                int k = good.cargo().ordinal();
                p.sea.computeIfAbsent(s, x -> new double[K])[k] += (in + out) * good.tonnesPerUnit() * ship;
                p.landed.computeIfAbsent(s, x -> new double[K])[k] += in * good.tonnesPerUnit() * ship;
                p.shipped.computeIfAbsent(s, x -> new double[K])[k] += out * good.tonnesPerUnit() * ship;
            }
        }
        PortDivision pd = fr.portDivision();
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            p.known[kind.ordinal()] = pd.isQuoteKnown(kind);
            p.quote[kind.ordinal()] = pd.getCrewQuote(kind);
        }
        return p;
    }

    static double dial(Game g) { return g.getEconomyManager().getTaxPolicy().getPortDues(); }

    /** The rule's target for a kind from the figures of the month just billed (the division's getters, the berths standing, the tally's boundary tonnes, the dial). */
    static double target(Game g, Ports.Cargo kind) {
        PortDivision pd = g.getSectors().freight().portDivision();
        double berths = Ports.berths(g.getBuildingManager())[kind.ordinal()];
        return PortDivision.targetOf(pd.getCrew(kind), pd.getTonnes(kind), berths,
                PortDivision.scarcityOf(g.getPorts().carriable(kind), berths), pd.getDuesRate(kind), dial(g));
    }

    /** ...and its crew's part alone, the part the quote walks (PortDivision.crewTargetOf()). */
    static double crewTarget(Game g, Ports.Cargo kind) {
        PortDivision pd = g.getSectors().freight().portDivision();
        double berths = Ports.berths(g.getBuildingManager())[kind.ordinal()];
        return PortDivision.crewTargetOf(pd.getCrew(kind), pd.getTonnes(kind), berths, PortDivision.scarcityOf(g.getPorts().carriable(kind), berths));
    }

    /**
     * The price a kind was billed at in the month just run: the crew quote in force before it (or the rule's own crew target
     * while none was struck), and the dues at the rate that month paid the city - the dues passed through (PortDivision).
     */
    static double billedAt(Game g, Plan p, Ports.Cargo kind) {
        PortDivision pd = g.getSectors().freight().portDivision();
        return (p.known[kind.ordinal()] ? p.quote[kind.ordinal()] : crewTarget(g, kind)) + pd.getDuesRate(kind) * dial(g);
    }

    static double sumOf(double[] a) {
        double t = 0;
        for (double v : a) t += v;
        return t;
    }

    static double lineOf(Sector s, String what) { return s.otherInputParts().getOrDefault(what, 0.0); }

    static void handling() {
        System.out.println("\n--- 4. handling, each leg once: a shipper pays its sea tonnes at the quote, Freight bills the sum ---");
        Game g = seaBase(GameFiles.scratch("landlordport-handling"));
        Freight fr = g.getSectors().freight();
        PortDivision pd = fr.portDivision();
        BuildingManager bm = g.getBuildingManager();
        double[] berths = Ports.berths(bm);
        int bulk = Ports.Cargo.DRY_BULK.ordinal();
        Plan p = plan(g);
        double landsBySea = 0, shipsBySea = 0;
        for (double[] a : p.landed.values()) landsBySea += sumOf(a);
        for (double[] a : p.shipped.values()) shipsBySea += sumOf(a);
        double noBerth = 0;
        for (Sector s : g.getSectors().all()) {
            if (s == fr) continue;
            for (Good good : Good.values()) {
                Traffic stream = good.traffic();
                if (stream == null || !stream.isFreight() || good.cargo() == null || berths[good.cargo().ordinal()] > 0) continue;
                noBerth += (s.unitsImported(good) + s.unitsExported(good)) * good.tonnesPerUnit();
            }
        }
        // A sector that buys at home as well as landing and shipping: the plant buys the mines' ore at home, and a tonne bought at home crosses no berth.
        final Sector plant = g.getSectors().heavyIndustry();
        double[] homeTonnes = new double[K];
        for (Good good : Good.values()) if (good.cargo() != null && good.traffic() != null) homeTonnes[good.cargo().ordinal()] += plant.unitsBoughtLocal(good) * good.tonnesPerUnit() * g.getPorts().seaShareOf(good, fr.getCarried()[good.traffic().ordinal()]);
        report("fixture: a Bulk Terminal stands, dry bulk goes by sea in a share in force, and shippers both land and ship by sea",
                berths[bulk] > 0 && g.getPorts().share(Ports.Cargo.DRY_BULK) > 0 && landsBySea > 0 && shipsBySea > 0 && pd.isQuoteKnown(Ports.Cargo.DRY_BULK),
                String.format("%,.0f t to land and %,.0f t to ship by sea, %d shippers; berths %,.0f t a month", landsBySea, shipsBySea, p.sea.size(), berths[bulk]));
        report("fixture: kinds with no berth cross the boundary too (their tonnes are never billed handling), and the plant buys ore at home as well",
                noBerth > 0 && homeTonnes[bulk] > 0 && p.sea.containsKey(plant),
                String.format("%,.1f t of kinds with no berth crossed; the plant bought %,.1f t of dry bulk at home", noBerth, homeTonnes[bulk]));

        quietly(g::toggleNextMonth);

        double[] quote = new double[K];
        for (Ports.Cargo kind : Ports.Cargo.values()) quote[kind.ordinal()] = billedAt(g, p, kind);
        double worst = 0, billedSum = 0, linesAll = 0, restTonnes = 0;
        String where = "";
        double[] others = new double[K];
        for (Sector s : g.getSectors().all()) {
            if (s == fr) continue;
            double got = lineOf(s, PortDivision.BILLED_AS);
            linesAll += got;
            double[] sea = p.sea.get(s);
            if (Sectors.CONSTRUCTION.equals(s.key())) continue;
            double want = 0;
            for (int k = 0; k < K; k++) {
                if (sea != null) {
                    want += sea[k] * quote[k];
                    others[k] += sea[k];
                }
            }
            billedSum += got;
            double miss = Math.abs(got - want);
            if (miss > worst) { worst = miss; where = String.format("%s billed D$%,.6fk against %,.6fk", s.key(), got, want); }
        }
        report("EACH SHIPPER'S \"Port handling\" IS ITS SEA TONNES OF EACH KIND x THE QUOTE IT WAS BILLED AT, to the cent",
                worst <= CENT && billedSum > 0, String.format("worst miss D$%.6fk (%s), %,.4fk billed to the shippers but the builders", worst, where.isEmpty() ? "none" : where, billedSum));
        // The builders buy their repair materials at the top of the month, before the haul (HaulageCheck.compare()): their tonnes are in no row between two months, so they are the remainder.
        double rest = 0, restWant = 0;
        for (int k = 0; k < K; k++) {
            double r = pd.getTonnes(Ports.Cargo.values()[k]) - others[k];
            restTonnes += r;
            restWant += Math.max(0, r) * quote[k];
        }
        Sector builders = g.getSectors().byKey(Sectors.CONSTRUCTION);
        rest = builders == null ? 0 : lineOf(builders, PortDivision.BILLED_AS);
        report("...the tonnes billed are the shippers' and the builders' only, and the builders' line is the remainder at the quote",
                restTonnes >= -1e-6 && toTheCent(rest, restWant),
                String.format("%,.1f t over the shippers' own; the builders paid D$%,.6fk against %,.6fk", restTonnes, rest, restWant));
        double billedBy = 0, tonnesBy = 0, seaTally = 0;
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            billedBy += pd.getBilled(kind);
            tonnesBy += pd.getTonnes(kind);
            seaTally += g.getPorts().seaIn(kind) + g.getPorts().seaOut(kind);
        }
        report("FREIGHT BILLS THE SHIPPERS' LINES SUMMED: getHandlingBilled(), its \"Port handling billed\" part and the kinds' bills, to the cent",
                toTheCent(pd.getHandlingBilled(), linesAll) && toTheCent(pd.getHandlingBilled(), billedBy)
                        && toTheCent(fr.otherRevenueParts().getOrDefault(PortDivision.REVENUE_NAME, 0.0), linesAll) && pd.getHandlingBilled() > 0,
                String.format("D$%,.4fk billed; the lines add to %,.4fk; the kinds' bills to %,.4fk", pd.getHandlingBilled(), linesAll, billedBy));
        assertTrue("...on the tonnes the ports' own tally counted: the sea tonnes the division billed are the berths' tally of them",
                Math.abs(tonnesBy - seaTally) <= 1e-9 * Math.max(1, seaTally) && tonnesBy > 0);
        boolean idleKinds = true;
        for (Ports.Cargo kind : Ports.Cargo.values()) if (berths[kind.ordinal()] == 0) idleKinds &= pd.getTonnes(kind) == 0 && pd.getBilled(kind) == 0;
        assertTrue("A KIND WITH NO BERTH PAYS NO HANDLING: its tonnes billed and its bill are nothing, whatever crossed", idleKinds);
        double plantWant = 0, plantIfHome = 0;
        for (int k = 0; k < K; k++) {
            plantWant += p.sea.get(plant)[k] * quote[k];
            plantIfHome += (p.sea.get(plant)[k] + homeTonnes[k]) * quote[k];
        }
        report("A TONNE BOUGHT AT HOME PAYS NO HANDLING: the plant's line is its landed and shipped tonnes only, and not those it bought at home as well",
                toTheCent(lineOf(plant, PortDivision.BILLED_AS), plantWant) && plantIfHome - plantWant > CENT && !toTheCent(lineOf(plant, PortDivision.BILLED_AS), plantIfHome),
                String.format("billed D$%,.6fk; its sea tonnes alone come to %,.6fk, with the %,.1f t bought at home %,.6fk", lineOf(plant, PortDivision.BILLED_AS), plantWant, homeTonnes[bulk], plantIfHome));
    }

    static void dues() {
        System.out.println("\n--- 5. the dues: tonnes x each kind's rate x the dial, to the treasury ---");
        GameFiles f1 = GameFiles.scratch("landlordport-dues1"), f0 = GameFiles.scratch("landlordport-dues0"), f2 = GameFiles.scratch("landlordport-dues2");
        Game one = seaBase(f1), zero = seaBase(f0), two = seaBase(f2);
        quietly(() -> {
            zero.getEconomyManager().getTaxPolicy().setPortDues(0);
            two.getEconomyManager().getTaxPolicy().setPortDues(2);
            one.toggleNextMonth();
            zero.toggleNextMonth();
            two.toggleNextMonth();
        });
        Freight fr = one.getSectors().freight();
        PortDivision pd = fr.portDivision();
        double want = 0;
        for (Ports.Cargo kind : Ports.Cargo.values()) want += pd.getTonnes(kind) * pd.getDuesRate(kind) * dial(one);
        double paid = lineOf(fr, PortDivision.DUES_NAME);
        report("FREIGHT'S \"Port dues\" ARE THE SUM OVER KINDS OF TONNES x THE KIND'S DUES RATE x THE DIAL, to the cent",
                want > 0 && toTheCent(paid, want) && toTheCent(pd.getDuesPaid(), want) && dial(one) == TaxPolicy.DEFAULT_PORT_DUES,
                String.format("D$%,.4fk against %,.4fk at the dial %.1f", paid, want, dial(one)));
        report("...and the treasury's line and the national accounts' are those dues, in the tax take",
                toTheCent(one.getEconomyManager().getPortDues(), want) && toTheCent(one.getEconomyManager().getNationalAccounts().getPortDues(), want),
                String.format("treasury D$%,.4fk, accounts %,.4fk", one.getEconomyManager().getPortDues(), one.getEconomyManager().getNationalAccounts().getPortDues()));
        Freight f0r = zero.getSectors().freight(), f2r = two.getSectors().freight();
        double dues0 = f0r.portDivision().getDuesPaid(), dues2 = f2r.portDivision().getDuesPaid();
        // The handling carries the dues at the dial (passed through), so the twins' crew's part is what they share.
        double crewPart = pd.getHandlingBilled() - pd.getDuesPaid();
        assertTrue("fixture: the twins are alike but for the dial: the same sea tonnes were billed at the same crew's part, and the dial-1 twin paid dues",
                toTheCent(f0r.portDivision().getHandlingBilled() - dues0, crewPart) && toTheCent(f2r.portDivision().getHandlingBilled() - dues2, crewPart)
                        && f0r.portDivision().getTonnes(Ports.Cargo.DRY_BULK) == pd.getTonnes(Ports.Cargo.DRY_BULK)
                        && f2r.portDivision().getTonnes(Ports.Cargo.DRY_BULK) == pd.getTonnes(Ports.Cargo.DRY_BULK) && pd.getDuesPaid() > 0);
        report("AT THE DIAL 0 NO DUES ARE PAID: no \"Port dues\" line in Freight's inputs, and nothing in the treasury's or the accounts'",
                dues0 == 0 && !f0r.otherInputParts().containsKey(PortDivision.DUES_NAME) && zero.getEconomyManager().getPortDues() == 0
                        && zero.getEconomyManager().getNationalAccounts().getPortDues() == 0,
                String.format("D$%,.4fk of dues", dues0));
        report("...AND AT 2 EXACTLY TWICE THE DIAL-1 TWIN'S, in Freight's line, the treasury's and the accounts'",
                toTheCent(dues2, 2 * pd.getDuesPaid()) && toTheCent(lineOf(f2r, PortDivision.DUES_NAME), 2 * paid)
                        && toTheCent(two.getEconomyManager().getPortDues(), 2 * one.getEconomyManager().getPortDues())
                        && toTheCent(two.getEconomyManager().getNationalAccounts().getPortDues(), 2 * one.getEconomyManager().getNationalAccounts().getPortDues()),
                String.format("D$%,.4fk against twice D$%,.4fk", dues2, pd.getDuesPaid()));
    }

    static void duesDefault() {
        System.out.println("\n--- 6. the dues' default: the annuity of the berths at the city's rate, over their month at DESIGN_USE ---");
        Game g = HaulageCheck.played(GameFiles.scratch("landlordport-annuity"), 14);
        quietly(() -> HaulageCheck.lay(g, "Bulk Terminal", 1));
        Freight fr = g.getSectors().freight();
        PortDivision pd = fr.portDivision();
        BuildingManager bm = g.getBuildingManager();
        double[] berths = Ports.berths(bm);
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            int k = kind.ordinal();
            double value = 0;
            for (BuildingsTemplate t : bm.getTemplates()) {
                if (!t.isPort() || t.berthCargo() != kind) continue;
                value += bm.getQuantity(t.getId()) * (t.getCashCost() + t.getConstructionMaterials() * bm.getConstructionMaterialPrice());
            }
            double r = pd.cityRate() / 12, n = PortDivision.PORT_LIFE_MONTHS;
            double annuity = r > 0 ? value * r / (1 - Math.pow(1 + r, -n)) : value / n;
            double want = berths[k] > 0 ? annuity / (berths[k] * PortDivision.DESIGN_USE) : 0;
            double got = pd.duesRateOf(kind, berths[k]);
            report("the " + kind + " default due is the annuity worked by hand: V r / (1 - (1+r)^-n) over the berths' month at DESIGN_USE",
                    Math.abs(got - want) <= 1e-12 * Math.max(1e-9, Math.abs(want)) && (berths[k] > 0) == (got > 0),
                    String.format("D$%.6fk a tonne against %.6fk (V %,.1fk, r %.5f a month)", got, want, value, r));
        }
        double v = 100_000, nn = PortDivision.PORT_LIFE_MONTHS;
        assertTrue("...and at no interest it is V / n, and a rate r the annuity",
                PortDivision.monthlyRecovery(v, 0, nn) == v / nn
                        && Math.abs(PortDivision.monthlyRecovery(v, .004, nn) - v * .004 / (1 - Math.pow(1.004, -nn))) <= 1e-12 * v
                        && PortDivision.monthlyRecovery(v, .004, nn) > v / nn);
        double curve = g.getDebtManager().curveRate((int) PortDivision.PORT_LIFE_MONTHS);
        assertTrue("THE CITY'S RATE IS ITS OWN PAPER'S AT THE BERTH'S LIFE: DebtManager.curveRate(PORT_LIFE_MONTHS), and not below nothing",
                pd.cityRate() == Math.max(0, curve) && PortDivision.PORT_LIFE_MONTHS == 360);

        Game owing = HaulageCheck.played(GameFiles.scratch("landlordport-owing"), 14);
        quietly(() -> HaulageCheck.lay(owing, "Bulk Terminal", 1));
        double[] b2 = Ports.berths(owing.getBuildingManager());
        PortDivision pdo = owing.getSectors().freight().portDivision();
        double calmRate = pdo.cityRate(), calmDue = pdo.duesRateOf(Ports.Cargo.DRY_BULK, b2[Ports.Cargo.DRY_BULK.ordinal()]);
        quietly(() -> owing.getDebtManager().addLongTermBond(5_000_000, (int) PortDivision.PORT_LIFE_MONTHS, 1, .04));
        double owingRate = pdo.cityRate(), owingDue = pdo.duesRateOf(Ports.Cargo.DRY_BULK, b2[Ports.Cargo.DRY_BULK.ordinal()]);
        report("A CITY OWING MORE PAYS A DEARER DUE: the same berths, a heavier debt, a dearer paper and a dearer due a tonne",
                owingRate > calmRate && owingDue > calmDue && calmDue > 0,
                String.format("rate %.5f -> %.5f, due D$%.6fk -> %.6fk a tonne", calmRate, owingRate, calmDue, owingDue));
    }

    static void price() {
        System.out.println("\n--- 7. the price: the first quote is the target, then a quarter of the way; the scarcity; the idle crew ---");
        double berth = 1000;
        assertTrue("the scarcity is 1 at or under the berths' month, the cap at twice and above, linear between",
                PortDivision.scarcityOf(berth / 2, berth) == 1 && PortDivision.scarcityOf(berth, berth) == 1
                        && PortDivision.scarcityOf(2 * berth, berth) == Freight.MAX_SCARCITY_MULTIPLE
                        && PortDivision.scarcityOf(3 * berth, berth) == Freight.MAX_SCARCITY_MULTIPLE
                        && Math.abs(PortDivision.scarcityOf(1.5 * berth, berth) - (1 + .5 * (Freight.MAX_SCARCITY_MULTIPLE - 1))) <= 1e-12
                        && PortDivision.scarcityOf(5 * berth, 0) == 1);
        Game g = HaulageCheck.played(GameFiles.scratch("landlordport-price"), 14);
        Freight fr = g.getSectors().freight();
        PortDivision pd = fr.portDivision();
        Ports.Cargo kind = Ports.Cargo.DRY_BULK;
        quietly(() -> HaulageCheck.lay(g, "Bulk Terminal", 1));
        assertTrue("fixture: a berth standing and no quote struck yet", Ports.berths(g.getBuildingManager())[kind.ordinal()] > 0 && !pd.isQuoteKnown(kind));
        int months = 0;
        while (!pd.isQuoteKnown(kind) && months < 4) {
            quietly(g::toggleNextMonth);
            months++;
        }
        double first = pd.getCrewQuote(kind), want = crewTarget(g, kind);
        report("THE FIRST QUOTE A KIND STRIKES, the month it has a berth and a crew, IS THE RULE'S TARGET from the month's own figures (its crew's part)",
                pd.isQuoteKnown(kind) && pd.getCrew(kind) > 0 && Math.abs(first - want) <= 1e-12 * Math.max(1e-9, want) && first > 0,
                String.format("D$%.6fk a tonne after %d month(s) against the target %.6fk (crew D$%,.4fk on %,.0f t)", first, months, want, pd.getCrew(kind), pd.getTonnes(kind)));
        quietly(g::toggleNextMonth);
        double second = pd.getCrewQuote(kind), target2 = crewTarget(g, kind);
        report("...AND THE NEXT WALKS REPRICE_SPEED OF THE WAY to the new target",
                Math.abs(second - (first + (target2 - first) * Freight.REPRICE_SPEED)) <= 1e-12 * Math.max(1e-9, Math.abs(second)),
                String.format("D$%.6fk -> %.6fk, the target %.6fk, a quarter of the way", first, second, target2));
        report("THE DUES ARE PASSED THROUGH AT THE MONTH'S OWN RATE: the kind's bill less its dues is its tonnes x the crew quote it was billed at, to the cent",
                toTheCent(pd.getBilled(kind) - pd.getDues(kind), pd.getTonnes(kind) * first) && pd.getDues(kind) > 0
                        && Math.abs(pd.getQuote(kind) - (second + pd.getDuesRate(kind) * dial(g))) <= 1e-12 * Math.max(1e-9, pd.getQuote(kind)),
                String.format("billed D$%,.4fk, dues %,.4fk, the crew's part %,.4fk on %,.0f t", pd.getBilled(kind), pd.getDues(kind), pd.getTonnes(kind) * first, pd.getTonnes(kind)));
        double tonnes = pd.getTonnes(kind), design = pd.getDesignTonnes(kind), berths = Ports.berths(g.getBuildingManager())[kind.ordinal()];
        assertTrue("fixture: the berth is worked below DESIGN_USE (the design tonnes are the berths' month x DESIGN_USE)",
                tonnes > 0 && tonnes < design && Math.abs(design - berths * PortDivision.DESIGN_USE) <= 1e-9 * design);
        report("A BERTH BELOW DESIGN USE IS CARRIED BY THE DIVISION: the month's handling billed is less than its crew plus its dues",
                pd.getBilled(kind) < pd.getCrew(kind) + pd.getDues(kind) && pd.getBilled(kind) > pd.getDues(kind) && pd.getCrew(kind) > 0,
                String.format("billed D$%,.4fk against a crew of %,.4fk and dues of %,.4fk", pd.getBilled(kind), pd.getCrew(kind), pd.getDues(kind)));
    }

    static void money() {
        System.out.println("\n--- 8. the money: the audit closes every month of a city working a port that pays dues ---");
        Game g = seaBase(GameFiles.scratch("landlordport-money"));
        Freight fr = g.getSectors().freight();
        double worst = 0, dues = 0, handling = 0;
        boolean everyMonth = true;
        for (int m = 0; m < 6; m++) {
            quietly(g::toggleNextMonth);
            worst = Math.max(worst, PortCheck.auditMiss(g));
            everyMonth &= fr.portDivision().getDuesPaid() > 0 && fr.portDivision().getHandlingBilled() > 0;
            dues += fr.portDivision().getDuesPaid();
            handling += fr.portDivision().getHandlingBilled();
        }
        assertTrue("fixture: a terminal stands and dues and handling were paid in every one of the months checked", everyMonth && dues > 0 && handling > 0);
        report("THE MONEY AUDIT'S RESIDUAL IS WITHIN ITS TOLERANCE every month: no dollar made or lost between shipper, Freight, the households and the treasury",
                worst <= 1, String.format("worst %.2e of the tolerance; D$%,.2fk of dues and %,.2fk of handling over six months", worst, dues, handling));
    }

    static void save() throws Exception {
        System.out.println("\n--- 9. the save: the dial in the policy state; the quotes, the month and the treasury's lines across a reload ---");
        TaxPolicy tp = new TaxPolicy();
        tp.setPortDues(1.5);
        double[] state = tp.getPolicyState();
        TaxPolicy back = new TaxPolicy();
        back.setPortDues(.25);
        boolean ok = back.restorePolicyState(state);
        assertTrue("the dial at 1.5 crosses getPolicyState() and restorePolicyState()", ok && back.getPortDues() == 1.5 && state.length == TaxPolicy.STATE_SLOTS);
        TaxPolicy old = new TaxPolicy();
        old.setPortDues(1.5);
        boolean oldOk = old.restorePolicyState(java.util.Arrays.copyOf(state, TaxPolicy.STATE_BEFORE_PORT_DUES));
        assertTrue("...and an array cut where the dial was added restores the default", oldOk && old.getPortDues() == TaxPolicy.DEFAULT_PORT_DUES);

        GameFiles files = GameFiles.scratch("landlordport-save");
        Game g = seaBase(files);
        quietly(() -> {
            g.getEconomyManager().getTaxPolicy().setPortDues(1.5);
            g.toggleNextMonth();
        });
        PortDivision pd = g.getSectors().freight().portDivision();
        assertTrue("fixture: the month paid dues at the dial 1.5 and struck a quote", pd.getDuesPaid() > 0 && dial(g) == 1.5 && pd.isQuoteKnown(Ports.Cargo.DRY_BULK));
        quietly(() -> g.saveGame(10, "landlordport"));
        Game again = new Game(files);
        quietly(() -> again.loadGameSave(10));
        PortDivision pb = again.getSectors().freight().portDivision();
        boolean same = again.getLoadFailure() == null;
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            same &= pb.getQuote(kind) == pd.getQuote(kind) && pb.isQuoteKnown(kind) == pd.isQuoteKnown(kind) && pb.getTonnes(kind) == pd.getTonnes(kind)
                    && pb.getBilled(kind) == pd.getBilled(kind) && pb.getDues(kind) == pd.getDues(kind) && pb.getCrew(kind) == pd.getCrew(kind)
                    && pb.getDuesRate(kind) == pd.getDuesRate(kind) && pb.getDesignTonnes(kind) == pd.getDesignTonnes(kind);
        }
        report("A CITY SAVED AND RELOADED READS THE SAME QUOTES, TONNES AND BILLS, to the bit", same && pd.getQuote(Ports.Cargo.DRY_BULK) > 0,
                String.format("quote D$%.6fk a tonne", pb.getQuote(Ports.Cargo.DRY_BULK)));
        report("...the same dues paid and handling billed, the treasury's tax take line and the national accounts' line, and the dial",
                pb.getDuesPaid() == pd.getDuesPaid() && pb.getHandlingBilled() == pd.getHandlingBilled()
                        && again.getEconomyManager().getPortDues() == g.getEconomyManager().getPortDues()
                        && again.getEconomyManager().getNationalAccounts().getPortDues() == g.getEconomyManager().getNationalAccounts().getPortDues()
                        && dial(again) == 1.5 && g.getEconomyManager().getPortDues() > 0,
                String.format("dues D$%,.4fk, handling %,.4fk", pb.getDuesPaid(), pb.getHandlingBilled()));
    }

    /* ================================================================
       THE REST OF THE BRIEF, FOR THE SLICES THAT BUILD IT
       ================================================================ */

    // 1. THE BULK BERTH: slice 1's (PortCheck holds it; this harness takes it over in FR3's last slice).
    // 2. THE SHIPS: slice 1's (the same).
    // 10. THE ADVICE: slice 4's.
    // 11. A REAL RUN: LongPlaytest's.

    public static void main(String[] args) throws Exception {
        crews();
        handling();
        dues();
        duesDefault();
        price();
        money();
        save();
        System.out.println();
        if (fails == 0) {
            System.out.println("LandlordPortCheck: the berths worked by Freight - the crews, the handling, the dues, the price, the money and the save (sections 1-2 and 10-11 are the other slices'): all passed");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }
}
