package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ST3's probe (2026-11-09): a town played by a scripted list of builds, no advisor, on ScaleCheck section 6's setup (the causes
 * of death held off, the goods' literals, the rate pinned), one line a month: what section 6 asks of its fixture month.
 *
 * Usage: ST3Probe <months> ; the plan in -Dst3.plan="every@from-to:Name*qty;..." (to optional), schools by
 * LongPlaytest.ensureSchools() every -Dst3.schools months (0 = never). Columns: month, people, a year before, places, homes,
 * households, posts, unemployment, rent burden, pull, unskilled wage, family rent, free sq ft, cash, homes the month built
 * (not the plan's).
 */
public class ST3Probe {

    static final String POWER = System.getProperty("st3.power", "Wind Farm");

    record Step(int every, int from, int to, String name, int qty) { }

    /** One of a kind at a time: built when its need is on and none is on site. */
    static final String SKIP = System.getProperty("st3.skip", "");

    static void ahead(Game g, boolean need, String name, StringBuilder did) {
        for (String t : SKIP.split(",")) if (!t.isEmpty() && name.contains(t)) return;
        if (need && g.getBuildingManager().underConstructionByName(name) == 0 && LongPlaytest.build(g, name, 1))
            did.append("+1 ").append(name).append(' ');
    }

    /** The services the advisor answers as throttles, each kept ahead by its own need: roads, clinics, builders, shops. */
    static void services(Game g, StringBuilder did) {
        BuildingManager b = g.getBuildingManager();
        long people = g.getPopulationManager().getPopulation();
        ahead(g, g.getRoadRatio() < 1, "Paved Road", did);
        ahead(g, g.getHealth().getBaselineRate() > Health.WELL_SERVED_RATE, "Walk-in Clinic", did);
        ahead(g, b.getTotalConstructionCapacity() < Math.max(1500, people * .02), "Construction Depot", did);
        ahead(g, people > b.getTotalStoreCoverage(), "Convenience Store", did);
        double homesAhead = Double.parseDouble(System.getProperty("st3.homes", "0"));
        double pressure = Double.parseDouble(System.getProperty("st3.pressure", "0"));
        if (pressure > 0) ahead(g, g.getSectors().realEstate().familyPressure() > pressure, "Low-Rise Apartments", did);
        if (homesAhead > 0) ahead(g, g.getHouseholdCapacity() < people * homesAhead, System.getProperty("st3.home", "Low-Rise Apartments"), did);
    }

    public static void main(String[] a) throws Exception {
        Locale.setDefault(Locale.CANADA);
        PrintStream real = System.out, quiet = new PrintStream(OutputStream.nullOutputStream());
        int months = Integer.parseInt(a[0]);
        List<Step> plan = new ArrayList<>();
        for (String s : System.getProperty("st3.plan", "").split(";")) {
            s = s.trim();
            if (s.isEmpty()) continue;
            String[] at = s.split(":");
            String[] when = at[0].split("@");
            int every = Integer.parseInt(when[0]);
            String[] range = (when.length > 1 ? when[1] : "0").split("-");
            int from = Integer.parseInt(range[0]), to = range.length > 1 ? Integer.parseInt(range[1]) : Integer.MAX_VALUE;
            String[] what = at[1].split("\\*");
            plan.add(new Step(every, from, to, what[0], Integer.parseInt(what[1])));
        }
        int schools = Integer.getInteger("st3.schools", 0);
        Path root = TempDirs.create("st3probe");
        Game g = new Game(new GameFiles(root.resolve("t"), root.resolve("t-no-legacy")), LongPlaytest.founding());
        g.holdCausesOfDeathOff();
        g.playOnLiterals();
        g.getForeignAccounts().pinRate(g.getForeignAccounts().getRate());
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        long t0 = System.nanoTime();
        List<Long> people = new ArrayList<>();
        try {
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            String ptax = System.getProperty("st3.ptax");
            if (ptax != null) g.getEconomyManager().getTaxPolicy().setPropertyTaxRate(Double.parseDouble(ptax));
            if (Boolean.getBoolean("st3.subsidy")) g.setAutoSubsidised(Sectors.CONSTRUCTION, true);
            if (!Boolean.getBoolean("st3.bare")) {
                LongPlaytest.build(g, "House", 40);
                LongPlaytest.build(g, "Convenience Store", 3);
                LongPlaytest.build(g, "Mixed Farm", 2);
            }
            real.println("month,people,yearBefore,places,homes,households,posts,unemp,burden,pull,wage,rent,freeSqFt,cash,built,energy,water,road,health,fpressure,planned");
            int stop = 0, next = Boolean.getBoolean("st3.advisor") ? 12 : Integer.MAX_VALUE;
            while (g.getMonth() < months) {
                int m = g.getMonth();
                StringBuilder did = new StringBuilder();
                if (m == next) {
                    // the playtest's rhythm (OrderSearchCheck.playtestRhythm()), its looks logged
                    if (stop == 0) { String w = LongPlaytest.advise(g); if (w != null) did.append("[").append(w).append("] "); next = m + 6; }
                    else {
                        LongPlaytest.ensureSchools(g);
                        String w = LongPlaytest.advise(g);
                        if (w != null) did.append("[").append(w).append("] ");
                        int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                        next = m + Math.max(1, skip);
                    }
                    stop++;
                }
                for (Step s : plan) {
                    if (m >= s.from() && m <= s.to() && (m - s.from()) % s.every() == 0) {
                        boolean ok = LongPlaytest.build(g, s.name(), s.qty());
                        did.append(ok ? "+" : "!").append(s.qty()).append(' ').append(s.name()).append(' ');
                    }
                }
                if (schools > 0 && m % schools == 0) LongPlaytest.ensureSchools(g);
                if (Boolean.getBoolean("st3.ground") && m % Integer.getInteger("st3.groundEvery", 1) == 0) LongPlaytest.keepGroundAhead(g);
                if (Boolean.getBoolean("st3.utilities")) {
                    if (g.getEnergyRatio() < 1 && g.getBuildingManager().underConstructionByName(POWER) == 0
                            && LongPlaytest.build(g, POWER, 1)) did.append("+1 ").append(POWER).append(' ');
                    if (g.getWaterRatio() < 1 && g.getBuildingManager().underConstructionByName("Water Treatment Plant") == 0
                            && LongPlaytest.build(g, "Water Treatment Plant", 1)) did.append("+1 water ");
                }
                if (Boolean.getBoolean("st3.services")) services(g, did);
                long homesBefore = g.getBuildingManager().getTotalHomes();
                g.simulateMonths(1);
                if (g.getMonth() == m) { real.println("stuck at " + m); break; }
                long pop = g.getPopulationManager().getPopulation();
                people.add(pop);
                long yearBefore = people.size() > 12 ? people.get(people.size() - 13) : 0;
                real.printf("%d,%d,%d,%d,%d,%d,%d,%.4f,%.4f,%.4f,%.2f,%.2f,%.0f,%.0f,%d,%.3f,%.3f,%.3f,%.4f,%.3f,%s%n", g.getMonth(), pop, yearBefore,
                        g.getHouseholdCapacity(), g.getBuildingManager().getTotalHomes(), (long) g.getSectors().realEstate().getHouseholdCount(),
                        g.getPopulationManager().getTotalJobs(), g.getPopulationManager().getUnemploymentRate(),
                        g.getHouseholds().getRentBurden(), g.getMigration().getLastAffordabilityPull(), g.getUnskilledWage(),
                        g.getSectors().realEstate().getRentPrice(), g.getLandManager().getAvailableSqFt(), g.getCash(),
                        g.getBuildingManager().getTotalHomes() - homesBefore, g.getEnergyRatio(), g.getWaterRatio(), g.getRoadRatio(), g.getHealth().getBaselineRate(), g.getSectors().realEstate().familyPressure(), did.toString().trim());
            }
        } finally {
            LongPlaytest.out = was;
            System.setOut(real);
        }
        java.util.TreeMap<String, Integer> count = new java.util.TreeMap<>();
        for (BuildingsStacks st : g.getBuildingManager().getStacksView()) count.merge(st.getName(), st.getQuantity(), Integer::sum);
        real.println("# buildings: " + count);
        java.util.TreeMap<String, String> stacks = new java.util.TreeMap<>();
        for (BuildingsStacks st : g.getBuildingManager().getStacksView()) stacks.merge(st.getName(), "" + st.getQuantity(), (x, y) -> x + "+" + y);
        real.println("# stacks: " + stacks);
        real.printf("# %d months in %.0f s%n", g.getMonth(), (System.nanoTime() - t0) / 1e9);
        System.exit(0);
    }
}
