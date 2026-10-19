package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;

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
 * that a getter or a constant could give. Ten claims, numbered as FR3.md
 * section 6 numbers them (its 10, a real run, is LongPlaytest's):
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
 *   4. HANDLING, EACH LEG ONCE. (a placeholder: slice 3's)
 *   5. THE DUES. (a placeholder: slice 3's)
 *   6. THE DUES' DEFAULT. (a placeholder: slice 3's)
 *   7. THE PRICE. (a placeholder: slice 3's)
 *   8. THE MONEY. (a placeholder: slice 3's)
 *   9. THE ADVICE. (a placeholder: slice 4's)
 *  10. A REAL RUN. (LongPlaytest's)
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
       THE REST OF THE BRIEF, FOR THE SLICES THAT BUILD IT
       ================================================================ */

    // 1. THE BULK BERTH: slice 1's (PortCheck holds it; this harness takes it over in FR3's last slice).
    // 2. THE SHIPS: slice 1's (the same).
    // 4. HANDLING, EACH LEG ONCE: slice 3's.
    // 5. THE DUES: slice 3's.
    // 6. THE DUES' DEFAULT: slice 3's.
    // 7. THE PRICE: slice 3's.
    // 8. THE MONEY: slice 3's.
    // 9. THE ADVICE: slice 4's.
    // 10. A REAL RUN: LongPlaytest's.

    public static void main(String[] args) {
        crews();
        System.out.println();
        if (fails == 0) {
            System.out.println("LandlordPortCheck: the crews are the city's berths worked by Freight (sections 1-2 and 4-10 are the later slices'): all passed");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }
}
