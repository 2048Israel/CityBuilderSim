package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;
import ham.citybuildersim.sectors.CementConcrete;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The farm's horses and the factory's steam, as forms a share of a sector runs.
 *
 * WHAT THIS HAS TO PROVE (R0c, 2026-11-05; research-1900 section 4). Both mechanics are SHARES, 0 to 1, of a sector that runs the 1900 way
 * (Agriculture.setHorseShare(), Sector.setSteamShare()); R1 will set them from each building's recipe and nothing sets them in a played
 * city yet, so what is checkable is that off the month is today's and that on each does what it says:
 *
 *   1. AT 0 THE MONTH IS TODAY'S, to the bit: two twin cities, one with both shares set to 0 on every sector every month, agree on the
 *      treasury, every sector's books and the city's power demand for twelve months, and the shares never put a good on a sector's list
 *   2. THE HORSES EAT THE FARM'S OWN CROP: captive, the horse share of HORSE_FEED_SHARE of what the farms made at the rate; no crop is bought
 *      or sold between the farm and itself, and the market saw the harvest less the feed
 *   3. THE HORSE FARM NEEDS MORE HANDS: its posts per tier, the payroll at full staffing and the posts the labour market is offered all
 *      read the horse posts alike
 *   4. STEAM TAKES THE FACTORY OFF THE GRID: its draw, its bill and the city's demand fall by the share, its coal need rises by the
 *      coal the engine burns for it, and the coal is bought at the rate
 *   5. A WORKS THAT ALREADY BURNS COAL ADDS THE STEAM COAL TO IT
 *   6. THE MONEY ADDS UP, with both forms on every sector, every month
 *
 * Every fixture CAUSES its condition: the shares are set, and the sector asked is one with buildings standing that draw power. The figures
 * are asserted against the model's own constants (HORSE_FEED_SHARE, HORSE_POSTS_FACTOR, STEAM_COAL_KG_A_KWH, HydroSites.KWH_PER_UNIT) and
 * its own getters, never numbers copied from the brief.
 */
public class Forms1900Check {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s%s%n", label, ok ? "OK" : "FAIL", ok || detail == null ? "" : "  " + detail);
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    /** A figure against the model's, to a relative tolerance (and an absolute floor for a figure that is nothing). */
    static void near(String label, double actual, double expected, double rel) {
        boolean ok = Math.abs(actual - expected) <= rel * Math.max(Math.abs(expected), 1e-12) + 1e-12;
        check(label, ok, String.format("%.9g != %.9g", actual, expected));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static void play(Game g, int months) {
        quietly(() -> g.simulateMonths(months));
    }

    /** The months a fixture city is played before anything is asked of it, so its farms and bakeries are running. */
    static final int SETTLE = 6;

    /** The months the twin cities are compared over, and the money audit is held over. */
    static final int MONTHS = 12;

    /** A town with farms for the horses and bakeries for the steam: roads, homes, groceries, power and water, the farms and the ovens standing at once. */
    static Game town(String label) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("Gravel Road"), 6, true);
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Mixed Farm"), 2, true);
            g.buildStack(b.getTemplateByName("Grain Farm"), 1, true);
            g.buildStack(b.getTemplateByName("Industrial Bakery"), 2, true);
            g.buildStack(b.getTemplateByName("Bakery"), 2, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            MaterialsCheck.handWorkers(g);
            g.simulateMonths(SETTLE);
        });
        return g;
    }

    /** Every figure the twins are compared on, with its name: the treasury, the city's power demand, and each sector's books and rate. */
    static void snapshot(Game g, List<String> names, List<Double> values) {
        names.add("treasury");
        values.add(g.getCash());
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        names.add("power demand");
        values.add(u.getConsumption());
        names.add("power billed draw");
        values.add(u.getBilledElectricityDraw());
        names.add("energy ratio");
        values.add(u.getEnergyRatio());
        for (Sector s : g.getSectors().all()) {
            Sector.Statement st = s.statement();
            names.add(s.key() + " revenue");
            values.add(st.revenue);
            names.add(s.key() + " inputs");
            values.add(st.inputs);
            names.add(s.key() + " payroll");
            values.add(st.payroll);
            names.add(s.key() + " electricity");
            values.add(st.electricity);
            names.add(s.key() + " operating rate");
            values.add(s.getOperatingRate());
            names.add(s.key() + " grid draw");
            values.add(s.getGridDraw());
            names.add(s.key() + " coal at capacity");
            values.add(s.getInputAtCapacity(Good.THERMAL_COAL));
            names.add(s.key() + " crops at capacity");
            values.add(s.getInputAtCapacity(Good.CROPS));
            names.add(s.key() + " posts offered");
            values.add((double) s.getPostsOffered());
        }
    }

    /** The first figure two cities do not agree on, to the bit, or null for none. */
    static String firstDifference(Game a, Game b) {
        List<String> names = new ArrayList<>(), namesB = new ArrayList<>();
        List<Double> x = new ArrayList<>(), y = new ArrayList<>();
        snapshot(a, names, x);
        snapshot(b, namesB, y);
        if (x.size() != y.size()) return "the snapshots differ in length";
        for (int i = 0; i < x.size(); i++) {
            if (Double.doubleToLongBits(x.get(i)) != Double.doubleToLongBits(y.get(i))) {
                return String.format("%s: %s != %s", names.get(i), x.get(i), y.get(i));
            }
        }
        return null;
    }

    /** What a city's posts are, per job type, as the labour market is offered them. */
    static long[] cityPosts(Game g) { return g.getBuildingManager().getTotalJobs(); }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());

        Game a = town("forms1900-a");
        Game b = town("forms1900-b");
        sectionOne(a, b);
        sectionTwoAndThree(a, b);
        Game c = town("forms1900-c");
        sectionFour(c);
        sectionFive();
        sectionSix(c, a);

        out.println(fails == 0 ? "\nAll Forms1900Check assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. at 0 the month is today's ============================ */

    static void sectionOne(Game a, Game b) {
        out.println("--- 1. at 0 the month is today's: twin cities, one with every share set to 0 each month, agree to the bit ---");
        Agriculture agriB = b.getSectors().agriculture();
        check("fixture: the farms stand and run", agriB.getCapacity(Good.CROPS) > 0 && agriB.getOperatingRate() > 0,
                String.format("capacity %.1f, rate %.3f", agriB.getCapacity(Good.CROPS), agriB.getOperatingRate()));
        check("fixture: the bakeries stand and draw power", b.getSectors().industry().getPowerDrawn() > 0);
        check("fixture: the twins start equal", firstDifference(a, b) == null, firstDifference(a, b));
        check("a city that never sets a share has the farms as no buyer of crops", !a.getSectors().agriculture().isUser(Good.CROPS)
                && !a.getSectors().agriculture().captive(Good.CROPS));
        String diverged = null;
        int month = 0;
        for (int m = 1; m <= MONTHS && diverged == null; m++) {
            agriB.setHorseShare(0);
            for (Sector s : b.getSectors().all()) s.setSteamShare(0);
            play(a, 1);
            play(b, 1);
            month = m;
            diverged = firstDifference(a, b);
        }
        check("...and for " + MONTHS + " months the treasury, every sector's books and the city's power demand agree to the bit",
                diverged == null, "month " + month + ", " + diverged);
        boolean listed = true;
        for (Sector s : b.getSectors().all()) listed &= s.goodsUsed().equals(a.getSectors().byKey(s.key()).goodsUsed());
        check("...and a share of 0 puts no good on any sector's list of inputs", listed && !agriB.isUser(Good.CROPS));
    }

    /* ============================ 2 and 3. the horses ============================ */

    /** Turns the twins into a horse farm at share 1 and one at half, and plays the month. */
    static void sectionTwoAndThree(Game a, Game b) {
        out.println("\n--- 2. the horses eat the farm's own crop, captive: the market sees the harvest less the feed ---");
        Agriculture full = a.getSectors().agriculture(), half = b.getSectors().agriculture();
        long[] base = a.getBuildingManager().getJobArrayBySector(full.key());
        long[] cityBase = cityPosts(a);
        String same = firstDifference(a, b);
        check("fixture: the twins are still equal, so the two shares are the only difference", same == null, same);

        full.setHorseShare(1);
        half.setHorseShare(0.5);
        check("a farm run on horses uses crops, and feeds itself first", full.isUser(Good.CROPS) && full.captive(Good.CROPS));
        long[] cityHorse = cityPosts(a);
        boolean cityAtStart = true;
        for (int i = 0; i < base.length; i++) cityAtStart &= cityHorse[i] - cityBase[i] == Math.round(base[i] * Agriculture.HORSE_POSTS_FACTOR) - base[i];
        check("the city's own count of posts, as the labour market is offered them, rises by the horse posts at once", cityAtStart);
        long[] farmPosts = full.postsPerTier();
        double feedAtCapacity = full.getHorseFeedAtCapacity();
        near("the crop need at nameplate is HORSE_FEED_SHARE of what the farms can make", feedAtCapacity,
                Agriculture.HORSE_FEED_SHARE * full.getCapacity(Good.CROPS), 1e-9);
        near("...the farms' crop need as the market reads it, getInputAtCapacity()", full.getInputAtCapacity(Good.CROPS), feedAtCapacity, 1e-9);
        near("at a share of one half the need at nameplate is half", half.getHorseFeedAtCapacity(), feedAtCapacity / 2, 1e-9);

        play(a, 1);
        play(b, 1);
        Sector.Output rowFull = full.outputRow(Good.CROPS), rowHalf = half.outputRow(Good.CROPS);
        check("fixture: the farms made crops in the month", rowFull != null && rowHalf != null && rowFull.capacity > 0 && rowHalf.capacity > 0);
        if (rowFull != null && rowHalf != null) {
            double madeFull = rowFull.produced + rowFull.exportBound + rowFull.idled + rowFull.captive;
            double madeHalf = rowHalf.produced + rowHalf.exportBound + rowHalf.idled + rowHalf.captive;
            check("fixture: the harvest is what the farms make at the rate", madeFull > 0 && madeHalf > 0);
            near("share 1: the captive crop is HORSE_FEED_SHARE of the harvest made at the rate", rowFull.captive,
                    Agriculture.HORSE_FEED_SHARE * madeFull, 1e-9);
            near("share 0.5: it is half of that share", rowHalf.captive, 0.5 * Agriculture.HORSE_FEED_SHARE * madeHalf, 1e-9);
            near("...so the feed at half the horses is half the feed per tonne harvested", rowHalf.captive / madeHalf,
                    0.5 * rowFull.captive / madeFull, 1e-9);
            near("the market saw the harvest less the feed: what the farms made for the shed, the ship and left idle is the rest",
                    rowFull.produced + rowFull.exportBound + rowFull.idled, madeFull - rowFull.captive, 1e-9);
        }
        check("no crop is bought by the farms: nothing at home, nothing from the world",
                full.unitsBoughtLocal(Good.CROPS) == 0 && full.unitsImported(Good.CROPS) == 0
                        && half.unitsBoughtLocal(Good.CROPS) == 0 && half.unitsImported(Good.CROPS) == 0);
        check("...and they bid for none: the feed is their own", full.inputRow(Good.CROPS) == null || full.inputRow(Good.CROPS).bid == 0);
        play(a, 1);
        play(b, 1);
        Sector.Split boughtCrops = full.statement().bought.get(Good.CROPS);
        check("...and the month's statement books no purchase of crops against the farms", boughtCrops == null || boughtCrops.total() == 0,
                boughtCrops == null ? null : String.format("%.1f", boughtCrops.total()));

        out.println("\n--- 3. the horse farm needs more hands: the posts, the payroll at full staffing and the labour market read them alike ---");
        check("fixture: the farms have posts to multiply", total(base) > 0);
        boolean exact = true, more = false;
        StringBuilder wrong = new StringBuilder();
        for (int i = 0; i < base.length; i++) {
            long want = Math.round(base[i] * Agriculture.HORSE_POSTS_FACTOR);
            if (farmPosts[i] != want) { exact = false; wrong.append(String.format(" tier %d: %d != %d", i, farmPosts[i], want)); }
            more |= want > base[i];
        }
        check("a horse farm's posts per tier at share 1 are the buildings' times HORSE_POSTS_FACTOR, rounded", exact, wrong.toString());
        check("...and there are more of them", more);
        long[] cityNow = cityPosts(a);
        full.setHorseShare(0);
        long[] atZero = full.postsPerTier();
        long[] cityAtZero = cityPosts(a);
        full.setHorseShare(1);
        long[] liveBase = a.getBuildingManager().getJobArrayBySector(full.key());
        check("at a share of 0 the posts per tier are the buildings' own, untouched", java.util.Arrays.equals(atZero, liveBase));
        boolean cityAgrees = true;
        for (int i = 0; i < cityNow.length; i++) {
            cityAgrees &= cityNow[i] - cityAtZero[i] == Math.round(liveBase[i] * Agriculture.HORSE_POSTS_FACTOR) - liveBase[i];
        }
        check("...and after the months the same extra posts, tier by tier, are in the city's count as are in the farm's payroll", cityAgrees);
        double[] rates = a.getEconomyManager().getWageRates();
        double fullStaffing = 0, atBase = 0;
        long[] offered = full.postsOfferedPerTier();
        for (int i = 0; i < offered.length && i < rates.length; i++) {
            fullStaffing += rates[i] * offered[i];
            atBase += rates[i] * liveBase[i];
        }
        check("the posts the farm offers after the month are the horse posts", total(offered) == total(full.postsPerTier()),
                total(offered) + " != " + total(full.postsPerTier()));
        near("the payroll at full staffing is the wage schedule on those posts", full.getPayrollAtFullStaffing(), fullStaffing, 1e-9);
        check("...and is more than the same schedule on the buildings' own posts", full.getPayrollAtFullStaffing() > atBase);
        check("the farm offers more posts at share 1 than at one half",
                full.getPostsOffered() >= half.getPostsOffered() && full.getPostsOffered() > total(base));
        near("a half-horse farm's posts sit between", half.getPostsOffered(), total(half.postsPerTier()), 1e-9);
    }

    static long total(long[] posts) {
        long t = 0;
        for (long p : posts) t += p;
        return t;
    }

    /* ============================ 4. steam takes the factory off the grid ============================ */

    static void sectionFour(Game c) {
        out.println("\n--- 4. steam takes the factory off the grid: its draw, its bill, the city's demand, and the coal it burns ---");
        Sector plant = c.getSectors().industry();
        UtilitiesHandler grid = c.getServicesManager().getUtilitiesHandler();
        check("fixture: the sector is the bakeries (Food Industry): it draws power from the grid and uses no coal today, with ovens standing",
                plant.getGridDraw() > 0 && plant.getInputAtCapacity(Good.THERMAL_COAL) == 0 && !plant.isUser(Good.THERMAL_COAL)
                        && plant.buildingsStanding() > 0 && plant.getPowerMade() == 0,
                String.format("grid draw %.1f, coal %.1f, ovens %d", plant.getGridDraw(), plant.getInputAtCapacity(Good.THERMAL_COAL), plant.buildingsStanding()));
        quietly(() -> c.getServicesManager().updateServices());
        double kw = plant.getGridDraw();
        double demand0 = grid.getConsumption(), billed0 = grid.getBilledElectricityDraw(), bill0 = plant.getElectricityCost();
        check("fixture: the sector is billed for the power (the bill is a price on the draw)", bill0 > 0 || c.getEconomyManager().getPricePerWatt() == 0);

        plant.setSteamShare(0.5);
        quietly(() -> c.getServicesManager().updateServices());
        near("share 0.5: half the draw is left on the grid", plant.getGridDraw(), kw / 2, 1e-9);
        near("...so the power bill is half", plant.getElectricityCost(), bill0 / 2, 1e-9);
        near("...the city's own power demand falls by the half of the draw", demand0 - grid.getConsumption(), kw / 2, 1e-9);
        near("...so does the billed draw the grid is invoiced on", billed0 - grid.getBilledElectricityDraw(), kw / 2, 1e-9);
        double coalHalf = plant.getSteamCoalAtCapacity();
        near("...and the coal it burns a month at nameplate is half the draw's kWh at STEAM_COAL_KG_A_KWH, in tonnes", coalHalf,
                0.5 * kw * HydroSites.KWH_PER_UNIT * Sector.STEAM_COAL_KG_A_KWH / 1000, 1e-9);
        check("...which is its whole coal need, there being no other", plant.isUser(Good.THERMAL_COAL));
        near("...as the market reads it", plant.getInputAtCapacity(Good.THERMAL_COAL), coalHalf, 1e-9);

        plant.setSteamShare(1);
        quietly(() -> c.getServicesManager().updateServices());
        check("share 1: the grid draw is 0", plant.getGridDraw() == 0);
        check("...the power bill is 0", plant.getElectricityCost() == 0);
        near("...the city's own power demand falls by exactly the kW it drew", demand0 - grid.getConsumption(), kw, 1e-9);
        near("...and the billed draw by the same", billed0 - grid.getBilledElectricityDraw(), kw, 1e-9);
        double coal = plant.getSteamCoalAtCapacity();
        near("...the coal need at capacity is the draw in kWh at STEAM_COAL_KG_A_KWH, in tonnes", coal,
                kw * HydroSites.KWH_PER_UNIT * Sector.STEAM_COAL_KG_A_KWH / 1000, 1e-9);
        near("...and twice share one half's", coal, 2 * coalHalf, 1e-9);

        play(c, 1);
        double need = plant.inputRow(Good.THERMAL_COAL) == null ? 0 : plant.inputRow(Good.THERMAL_COAL).needed;
        double bought = plant.unitsBoughtLocal(Good.THERMAL_COAL) + plant.unitsImported(Good.THERMAL_COAL);
        check("fixture: the sector ran the month and ordered coal", need > 0 && plant.getOperatingRate() > 0,
                String.format("needed %.3f, rate %.3f", need, plant.getOperatingRate()));
        near("after a month the coal it needed is its need at capacity at the rate it ran", need,
                plant.getInputAtCapacity(Good.THERMAL_COAL) * plant.getOperatingRate(), 1e-6);
        near("...and what it bought, at home or from the world, is that need", bought, need, 1e-9);
        check("the mines hold none, so the dock supplies it: the coal came in from the world",
                plant.unitsImported(Good.THERMAL_COAL) > 0 || plant.unitsBoughtLocal(Good.THERMAL_COAL) > 0);
        check("its power bill for the month is 0 and the grid is billed none of it", plant.getElectricityCost() == 0 && plant.getGridDraw() == 0);
    }

    /* ============================ 5. a works that already burns coal ============================ */

    static void sectionFive() {
        out.println("\n--- 5. a works that already burns coal adds the steam coal to it: Cement & Concrete ---");
        Game g = new Game(GameFiles.scratch("forms1900-cement"));
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            g.buildStack(g.getBuildingManager().getTemplate(CementConcrete.CEMENT_PLANT), 1, true);
        });
        CementConcrete works = g.getSectors().cementConcrete();
        BuildingsTemplate plant = g.getBuildingManager().getTemplate(CementConcrete.CEMENT_PLANT);
        double own = works.getInputAtCapacity(Good.THERMAL_COAL);
        check("fixture: a Cement Plant stands, and the works use coal today without any steam", works.buildingsStanding() == 1 && own > 0
                && works.isUser(Good.THERMAL_COAL), String.format("coal %.1f", own));
        near("...its own coal need is its building's", own, plant.uses(Good.THERMAL_COAL), 1e-9);
        double kw = plant.powerBought();
        check("fixture: the plant draws power from the grid", kw > 0);
        works.setSteamShare(1);
        near("at share 1 its coal need is its building's own plus the steam coal", works.getInputAtCapacity(Good.THERMAL_COAL),
                own + kw * HydroSites.KWH_PER_UNIT * Sector.STEAM_COAL_KG_A_KWH / 1000, 1e-9);
        near("...the steam coal alone being getSteamCoalAtCapacity()", works.getSteamCoalAtCapacity(),
                kw * HydroSites.KWH_PER_UNIT * Sector.STEAM_COAL_KG_A_KWH / 1000, 1e-9);
        works.setSteamShare(0);
        check("and at 0 again its need is its building's own, to the bit", works.getInputAtCapacity(Good.THERMAL_COAL) == own);
    }

    /* ============================ 6. the money adds up ============================ */

    static void sectionSix(Game c, Game untouched) {
        out.println("\n--- 6. the money adds up: both forms on every sector, the audit closed every month ---");
        Agriculture farms = c.getSectors().agriculture();
        farms.setHorseShare(1);
        for (Sector s : c.getSectors().all()) s.setSteamShare(1);
        int closed = 0;
        String first = null;
        for (int m = 1; m <= MONTHS; m++) {
            play(c, 1);
            MoneyAudit.Result r = c.getLastMoneyAudit();
            if (r != null && Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved())) closed++;
            else if (first == null) first = r == null ? "month " + m + ": no audit" : "month " + m + ": " + r;
        }
        check("the month's money audit closes in each of " + MONTHS + " months with horses and steam on", closed == MONTHS, first);
        Sector.Output feed = farms.outputRow(Good.CROPS);
        Sector bakeries = c.getSectors().industry();
        check("the forms ran: the farms fed their horses from their own harvest",
                farms.captive(Good.CROPS) && feed != null && feed.captive > 0, feed == null ? "no row" : String.format("%.3f", feed.captive));
        check("...and the bakeries burned coal, bought at home or from the world",
                bakeries.unitsBoughtLocal(Good.THERMAL_COAL) + bakeries.unitsImported(Good.THERMAL_COAL) > 0);
        check("...and the comparison has teeth: the city with the forms on differs from the untouched twin", firstDifference(c, untouched) != null);
    }
}
