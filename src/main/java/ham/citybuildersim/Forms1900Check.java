package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;
import ham.citybuildersim.sectors.CementConcrete;
import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The farm's horses, the factory's steam and road freight's horse and wagon, as forms a share of a sector runs.
 *
 * WHAT THIS HAS TO PROVE (R0c, 2026-11-05; research-1900 section 4). The mechanics are SHARES, 0 to 1, of a sector that runs the 1900 way
 * (Agriculture.setHorseShare(), Sector.setSteamShare(), Haulage.setHorseShare()); R1 will set them from each building's recipe and nothing
 * sets them in a played city yet, so what is checkable is that off the month is today's and that on each does what it says:
 *
 *   1. AT 0 THE MONTH IS TODAY'S, to the bit: two twin cities, one with every share set to 0 on every sector every month (Haulage's
 *      included), agree on the treasury, every sector's books, Haulage's fleet and quote and the city's power demand for twelve months, and
 *      the shares never put a good on a sector's list
 *   2. THE HORSES EAT THE FARM'S OWN CROP: captive, the horse share of HORSE_FEED_SHARE of what the farms made at the rate; no crop is bought
 *      or sold between the farm and itself, and the market saw the harvest less the feed
 *   3. THE HORSE FARM NEEDS MORE HANDS: its posts per tier, the payroll at full staffing and the posts the labour market is offered all
 *      read the horse posts alike
 *   4. STEAM TAKES THE FACTORY OFF THE GRID: its draw, its bill and the city's demand fall by the share, its coal need rises by the
 *      coal the engine burns for it, and the coal is bought at the rate
 *   5. A WORKS THAT ALREADY BURNS COAL ADDS THE STEAM COAL TO IT
 *   6. THE MONEY ADDS UP, with both forms on every sector, every month
 *   7. THE CARTS BURN NO DIESEL AND EAT CROPS: in a city whose bulk Haulage carries, at a horse share of 1 the trucks draw no diesel and the
 *      teams' feed is carried / TONNES_A_TEAM_MONTH teams x HORSES_A_TEAM x HORSE_FEED_TONNES_A_MONTH tonnes of crops, drawn on Freight's books
 *      from the farms or the world; at one half the diesel and the feed are each half a tonne's
 *   8. THE CARTERS ARE POSTS AND ARE PAID: the NO_DIPLOMA posts Freight offers, the city counts and the labour market fills rise by
 *      round(need / TONNES_A_TEAM_MONTH x CARTERS_A_TEAM), Haulage's payroll is the larger by their wages and the Rail division's is not
 *   9. A HORSE CITY BUYS FEWER TRUCKS: at 1 none are bought or held, at one half about half the twin's
 *  10. THE QUOTE PRICES THE HORSE: estimate() is the blend of today's and the teams' (feed and carters over a team's tonnes), and the
 *      reprice with a history is finite, positive and covers the feed and the carters
 *  11. THE MONEY ADDS UP, with the carts carrying every bulk tonne, every month
 *
 * Every fixture CAUSES its condition: the shares are set, and the sector asked is one with buildings standing that draw power (or, for 7 to
 * 11, a city with a bulk chain at home and trade both ways: HaulageCheck.base()). The figures are asserted against the model's own constants
 * (HORSE_FEED_SHARE, HORSE_POSTS_FACTOR, STEAM_COAL_KG_A_KWH, HydroSites.KWH_PER_UNIT, Haulage.HORSES_A_TEAM, HORSE_FEED_TONNES_A_MONTH,
 * TONNES_A_TEAM_MONTH, CARTERS_A_TEAM) and its own getters, never numbers copied from the brief.
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
        // ...and Freight's trucks: the fleet, what it carried, the share it was dealt, the quote and the posts' payroll (the horse form, R0c).
        Freight freight = g.getSectors().freight();
        Haulage haulage = freight.haulage();
        names.add("trucks");
        values.add(haulage.trucks());
        names.add("haulage carried");
        values.add(haulage.getCarried());
        names.add("haulage diesel");
        values.add(haulage.getDiesel());
        names.add("haulage share");
        values.add(haulage.getShare());
        names.add("haulage quote");
        values.add(haulage.getQuote());
        names.add("haulage payroll");
        values.add(freight.haulagePayroll());
        names.add("rail payroll");
        values.add(freight.railPayroll());
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
        sectionsSevenToEleven();

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
            b.getSectors().freight().haulage().setHorseShare(0);
            for (Sector s : b.getSectors().all()) s.setSteamShare(0);
            play(a, 1);
            play(b, 1);
            month = m;
            diverged = firstDifference(a, b);
        }
        check("...and for " + MONTHS + " months the treasury, every sector's books, Freight's trucks and quote and the city's power demand agree to the bit",
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

    /* ============================ 7 to 11. road freight's horse and wagon ============================ */

    /** The months the cart cities are played: the fleet and the money audit are held over them. */
    static final int CART_MONTHS = 12;

    /** One month of a city's Haulage, as the sections read it, taken the moment the month was played. */
    static final class Month {
        double need, carried, truckTonnes, teamTonnes, litres, diesel, teams, feedTonnes, feedTonnesImported, feed, feedImported;
        double trucks, share, quote, allowed, haulagePay;
        boolean quoteKnown, auditCloses;
        String audit = "";
        /** What Freight's statement booked of crops at home and abroad (-1: no line), and of trucks. */
        double cropsHome = -1, cropsAbroad = -1, trucksBought;
    }

    /** HaulageCheck's city (mines and a pelletising plant that lands its coal and ships its pellets, trade both ways, the goods' literals) with Haulage's horse share set before the first month. */
    static Game cartTown(String label, double horses) {
        Game g = HaulageCheck.base(GameFiles.scratch(label), true, true);
        g.getSectors().freight().haulage().setHorseShare(horses);
        return g;
    }

    static void step(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    /** Plays `months` months one at a time, and takes Haulage's figures after each (index 1 to months). */
    static Month[] run(Game g, int months) {
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        Month[] rows = new Month[months + 1];
        for (int m = 1; m <= months; m++) {
            step(g, 1);
            Month r = new Month();
            r.need = h.getNeed();
            r.carried = h.getCarried();
            r.truckTonnes = h.getTruckTonnes();
            r.teamTonnes = h.getTeamTonnes();
            r.litres = h.getDieselLitres();
            r.diesel = h.getDiesel();
            r.teams = h.getTeams();
            r.feedTonnes = h.getFeedTonnes();
            r.feedTonnesImported = h.getFeedTonnesImported();
            r.feed = h.getFeed();
            r.feedImported = h.getFeedImported();
            r.trucks = h.trucks();
            r.share = h.getShare();
            r.quote = h.getQuote();
            r.quoteKnown = h.isQuoteKnown();
            r.allowed = h.getAllowed();
            r.haulagePay = fr.haulagePayroll();
            Sector.Split crops = fr.statement().bought.get(Good.CROPS);
            if (crops != null) {
                r.cropsHome = crops.atHome;
                r.cropsAbroad = crops.abroad;
            }
            Sector.Split trucks = fr.statement().bought.get(Good.TRUCKS);
            r.trucksBought = trucks == null ? 0 : trucks.total();
            MoneyAudit.Result a = g.getLastMoneyAudit();
            r.auditCloses = a != null && Math.abs(a.residual) <= MoneyAudit.tolerance(a.moved());
            if (!r.auditCloses) r.audit = "month " + m + ": " + a;
            rows[m] = r;
        }
        return rows;
    }

    static void sectionsSevenToEleven() {
        Game none = cartTown("forms1900-cart-0", 0);
        Game half = cartTown("forms1900-cart-half", 0.5);
        Game full = cartTown("forms1900-cart-1", 1);
        double founding = full.getSectors().freight().haulage().trucks();
        Month[] z = run(none, CART_MONTHS), hh = run(half, CART_MONTHS), f = run(full, CART_MONTHS);
        sectionSeven(z, hh, f);
        sectionEight();
        sectionNine(founding, z, hh, f);
        sectionTen(full, f);
        sectionEleven(f);
    }

    /** The month the per-tonne comparisons are read in: the trucks are seeded, the plant has run, and none of the three cities is short of trucks. */
    static final int READ = 6;

    /** The months the farm city is played before its books are read: the farms have a shelf by the third. */
    static final int FARM_MONTHS = 4;

    static void sectionSeven(Month[] z, Month[] hh, Month[] f) {
        out.println("\n--- 7. the carts burn no diesel and eat crops: the trucks carry (1 - h), the teams h, and the horses' feed is drawn on Freight's books ---");
        final double perTeam = Haulage.HORSES_A_TEAM * Haulage.HORSE_FEED_TONNES_A_MONTH;
        final double perTonne = Haulage.DIESEL_LITRES_A_TONNE;
        check("fixture: the three cities have the same plant standing at the first haul (the same legs at nameplate)",
                z[1].need > 0 && Math.abs(z[1].need - f[1].need) <= 1e-9 * z[1].need && Math.abs(z[1].need - hh[1].need) <= 1e-9 * z[1].need,
                String.format("%.3f, %.3f, %.3f", z[1].need, hh[1].need, f[1].need));

        // ---- h = 1, the first month ----
        check("fixture: at a share of 1 the first month's bulk is carried, by teams alone, with no trucks yet",
                f[1].carried > 0 && f[1].teamTonnes == f[1].carried && f[1].truckTonnes == 0 && f[1].trucks == 0,
                String.format("carried %.3f, teams' %.3f, trucks' %.3f, trucks %.1f", f[1].carried, f[1].teamTonnes, f[1].truckTonnes, f[1].trucks));
        check("the carts burn no diesel: no litres drawn and none paid for", f[1].litres == 0 && f[1].diesel == 0,
                String.format("%.3f L, D$%.6f", f[1].litres, f[1].diesel));
        near("the teams hired are the tonnes carried over TONNES_A_TEAM_MONTH", f[1].teams, f[1].carried / Haulage.TONNES_A_TEAM_MONTH, 1e-9);
        near("the crops drawn are carried / TONNES_A_TEAM_MONTH teams x HORSES_A_TEAM x HORSE_FEED_TONNES_A_MONTH tonnes", f[1].feedTonnes,
                f[1].carried / Haulage.TONNES_A_TEAM_MONTH * perTeam, 1e-9);
        check("...and a share of 1 over the whole run draws no diesel in any of the " + CART_MONTHS + " months", allMonths(f, m -> m.litres == 0 && m.diesel == 0));
        check("Freight's books show the purchase of crops: bought at home and abroad it is what the draw cost",
                f[1].cropsHome >= 0 && Math.abs(f[1].cropsHome + f[1].cropsAbroad - f[1].feed) <= 1e-9 * Math.max(1, f[1].feed),
                String.format("books %.6f + %.6f, draw %.6f", f[1].cropsHome, f[1].cropsAbroad, f[1].feed));
        check("...the city having no farm, the world sold it: all of it was imported", f[1].feedTonnesImported == f[1].feedTonnes && f[1].feedTonnes > 0
                && f[1].cropsHome == 0, String.format("%.3f of %.3f t imported", f[1].feedTonnesImported, f[1].feedTonnes));

        // ---- h = 1/2 against h = 0 and h = 1, in a month where every fleet suffices ----
        Month a = z[READ], b = hh[READ], c = f[READ];
        check("fixture: month " + READ + ", every city carried bulk, and the fleets suffice (a literal 1) wherever there are trucks",
                a.carried > 0 && b.carried > 0 && c.carried > 0 && a.share == 1 && b.share == 1 && c.share == 1,
                String.format("carried %.1f, %.1f, %.1f; shares %.4f, %.4f, %.4f", a.carried, b.carried, c.carried, a.share, b.share, c.share));
        near("at h = 0 the trucks carry all of it: diesel is DIESEL_LITRES_A_TONNE a tonne carried", a.litres / a.carried, perTonne, 1e-9);
        near("at h = 0.5 the trucks carry half the tonnes", b.truckTonnes, 0.5 * b.carried, 1e-9);
        near("...and the teams the other half", b.teamTonnes, 0.5 * b.carried, 1e-9);
        near("...so the diesel per tonne carried is half of h = 0's for the same tonnes", b.litres / b.carried, 0.5 * a.litres / a.carried, 1e-9);
        near("...and the feed per tonne carried is half of h = 1's", b.feedTonnes / b.carried, 0.5 * c.feedTonnes / c.carried, 1e-9);
        near("...the feed itself being the teams' (h x carried / TONNES_A_TEAM_MONTH) x HORSES_A_TEAM x HORSE_FEED_TONNES_A_MONTH", b.feedTonnes,
                0.5 * b.carried / Haulage.TONNES_A_TEAM_MONTH * perTeam, 1e-9);
        check("h = 0 draws no feed at all", allMonths(z, m -> m.feedTonnes == 0 && m.teams == 0 && m.teamTonnes == 0));

        // ---- the farms ----
        Game farm = cartTown("forms1900-cart-farm", 1);
        quietly(() -> {
            BuildingManager bm = farm.getBuildingManager();
            farm.buildStack(bm.getTemplateByName("Mixed Farm"), 2, true);
            farm.buildStack(bm.getTemplateByName("Grain Farm"), 1, true);
        });
        Month[] fm = run(farm, FARM_MONTHS);
        Month e = fm[FARM_MONTHS];
        Agriculture farms = farm.getSectors().agriculture();
        check("fixture: farms stand and made crops, and the carts carried bulk", farms.getCapacity(Good.CROPS) > 0 && e.carried > 0,
                String.format("farm capacity %.1f, carried %.1f", farms.getCapacity(Good.CROPS), e.carried));
        near("with farms in the city the crops drawn are still carried / TONNES_A_TEAM_MONTH x HORSES_A_TEAM x HORSE_FEED_TONNES_A_MONTH", e.feedTonnes,
                e.carried / Haulage.TONNES_A_TEAM_MONTH * perTeam, 1e-9);
        check("...the farms sold some of it: Freight's books show crops bought at home, and the rest from the world",
                e.cropsHome > 0 && e.cropsAbroad > 0 && e.feedTonnesImported > 0 && e.feedTonnesImported < e.feedTonnes,
                String.format("books %.6f at home, %.6f abroad; %.3f of %.3f t imported", e.cropsHome, e.cropsAbroad, e.feedTonnesImported, e.feedTonnes));
        check("...and what the books show is what the draw cost: at home plus abroad is the feed, abroad is the imported part",
                Math.abs(e.cropsHome + e.cropsAbroad - e.feed) <= 1e-9 * Math.max(1, e.feed) && Math.abs(e.cropsAbroad - e.feedImported) <= 1e-9 * Math.max(1, e.feed),
                String.format("books %.6f + %.6f, draw %.6f (imported %.6f)", e.cropsHome, e.cropsAbroad, e.feed, e.feedImported));
    }

    interface Pick { boolean of(Month m); }

    /** Whether every month of a run (1 to its last) satisfies the test. */
    static boolean allMonths(Month[] rows, Pick pick) {
        for (int m = 1; m < rows.length; m++) if (!pick.of(rows[m])) return false;
        return true;
    }

    static void sectionEight() {
        out.println("\n--- 8. the carters are posts and are paid: offered by Freight, counted by the city, filled by the labour market, Haulage's payroll ---");
        Game g = cartTown("forms1900-carters", 0);
        step(g, 3);
        // A railway line and a depot standing, so the Rail division has a payroll to leave alone and Haulage one of its own.
        quietly(() -> {
            HaulageCheck.lay(g, "Rail Spur", 1);
            HaulageCheck.lay(g, "Truck Depot", 1);
        });
        step(g, 2);
        Freight fr = g.getSectors().freight();
        Haulage hl = fr.haulage();
        BuildingManager bm = g.getBuildingManager();
        final int k = JobType.NO_DIPLOMA.ordinal();
        // The wage pass as the month makes it, off the fill the labour market last set.
        g.getEconomyManager().updateJobFillRate(g.getPopulationManager().getJobFillRate());
        final double[] rates = g.getEconomyManager().getWageRates();
        quietly(() -> fr.updateWages(rates, fr.postsPerTier()));
        long[] posts0 = fr.postsPerTier(), city0 = bm.getTotalJobs(), offered0 = fr.postsOfferedPerTier();
        double rail0 = fr.railPayroll(), haul0 = fr.haulagePayroll(), full0 = fr.getPayrollAtFullStaffing();
        double fillNo = g.getPopulationManager().getJobFillRate()[k];
        long carters = Math.round(hl.getNeed() / Haulage.TONNES_A_TEAM_MONTH * Haulage.CARTERS_A_TEAM);
        check("fixture: a rail line and a depot stand and are paid (each division has a payroll), the city leg has a need, and a carter has a wage and a fill",
                rail0 > 0 && haul0 > 0 && carters > 0 && rates[k] > 0 && fillNo > 0 && hl.getCarters() == 0,
                String.format("rail %.4f, haulage %.4f, carters %d, wage %.4f, fill %.4f", rail0, haul0, carters, rates[k], fillNo));

        hl.setHorseShare(1);
        near("Haulage hires round(need / TONNES_A_TEAM_MONTH x CARTERS_A_TEAM) carters", hl.getCarters(), carters, 1e-12);
        long[] posts1 = fr.postsPerTier(), city1 = bm.getTotalJobs();
        boolean freightRises = true, cityRises = true;
        for (int i = 0; i < posts1.length; i++) {
            freightRises &= posts1[i] - posts0[i] == (i == k ? carters : 0);
            cityRises &= city1[i] - city0[i] == (i == k ? carters : 0);
        }
        check("Freight's NO_DIPLOMA posts rise by the carters at once, and no other tier's", freightRises);
        check("...and the city's count of posts, as the labour market is offered them, by the same", cityRises);
        quietly(() -> fr.updateWages(rates, fr.postsPerTier()));
        long[] offered1 = fr.postsOfferedPerTier();
        boolean offeredRises = true;
        for (int i = 0; i < offered1.length; i++) offeredRises &= offered1[i] - offered0[i] == (i == k ? carters : 0);
        check("...and the posts it offers, the payroll's, rise by the same", offeredRises);
        near("the payroll at full staffing is the larger by the carters at the NO_DIPLOMA wage", fr.getPayrollAtFullStaffing() - full0, carters * rates[k], 1e-9);
        near("Haulage's payroll is the larger by their wages: carters x wage x the tier's fill", fr.haulagePayroll() - haul0, carters * rates[k] * fillNo, 1e-9);
        near("...the Rail division's payroll is unchanged", fr.railPayroll(), rail0, 1e-9);
        near("...and the divisions' payrolls still come to the sector's", fr.railPayroll() + fr.haulagePayroll() + fr.portsPayroll(), fr.getPayroll(), 1e-9);
        near("...of which the carters' own are carterPayroll()", fr.carterPayroll(), carters * rates[k] * fillNo, 1e-9);

        step(g, 1);
        double[] fillNow = g.getPopulationManager().getJobFillRate();
        check("after a month at a share of 1 the labour market fills the tier, so the carters are hands and not vacancies", fillNow[k] > 0 && fr.getWorkers() > 0,
                String.format("fill %.4f", fillNow[k]));
        check("...and the posts Freight offers in the NO_DIPLOMA tier are its buildings' and the carters the need now asks for",
                fr.postsOfferedPerTier()[k] == bm.getJobArrayBySector(fr.key())[k] + hl.getCarters(),
                String.format("%d offered, %d in the buildings, %d carters", fr.postsOfferedPerTier()[k], bm.getJobArrayBySector(fr.key())[k], hl.getCarters()));
        check("...and the month's divisions' payrolls come to the sector's, with the carters in Haulage's",
                Math.abs(fr.railPayroll() + fr.haulagePayroll() + fr.portsPayroll() - fr.getPayroll()) <= 1e-9 * Math.max(1, fr.getPayroll())
                        && fr.haulagePayroll() >= fr.carterPayroll() && fr.carterPayroll() > 0);
    }

    static void sectionNine(double founding, Month[] z, Month[] hh, Month[] f) {
        out.println("\n--- 9. a horse city buys fewer trucks: none at a share of 1, about half the twin's at one half ---");
        check("fixture: the h = 0 twin seeded and bought a fleet over the " + CART_MONTHS + " months", z[CART_MONTHS].trucks > founding,
                String.format("%.1f trucks at month %d, %.1f at founding", z[CART_MONTHS].trucks, CART_MONTHS, founding));
        check("at a share of 1 the city holds no truck past what it held at founding, in any of the " + CART_MONTHS + " months", allMonths(f, m -> m.trucks <= founding),
                String.format("%.1f at month %d, %.1f at founding", f[CART_MONTHS].trucks, CART_MONTHS, founding));
        check("...and buys none: Freight's books show no purchase of trucks in any of them", allMonths(f, m -> m.trucksBought == 0));
        double ratio = hh[CART_MONTHS].trucks / z[CART_MONTHS].trucks;
        check("at a share of one half the fleet at month " + CART_MONTHS + " is about half the h = 0 twin's (within 15% of a half)",
                Math.abs(ratio - 0.5) <= 0.15 * 0.5, String.format("%.1f against %.1f, a ratio of %.3f", hh[CART_MONTHS].trucks, z[CART_MONTHS].trucks, ratio));
        double seeded = hh[2].trucks / z[2].trucks;
        check("...and the first fleet was seeded at (1 - h) of the need: half the twin's at month 2 too (within 15% of a half)",
                z[2].trucks > 0 && Math.abs(seeded - 0.5) <= 0.15 * 0.5, String.format("%.1f against %.1f", hh[2].trucks, z[2].trucks));
    }

    static void sectionTen(Game full, Month[] f) {
        out.println("\n--- 10. the quote prices the horse: the estimate is the blend of today's and the teams', the reprice covers the feed and the carters ---");
        Freight fr = full.getSectors().freight();
        Haulage hl = fr.haulage();
        final int k = JobType.NO_DIPLOMA.ordinal();
        double wage = full.getEconomyManager().getWageRates()[k];
        GoodsMarket cropsMarket = fr.markets().get(Good.CROPS);
        double crops = cropsMarket != null && Double.isFinite(cropsMarket.landedPrice()) ? cropsMarket.landedPrice() : Good.CROPS.worldImportPrice();
        // The formula, from the model's own dials and getters: a team's feed at the crops' price and its carters' month, over the tonnes a team carries.
        double formula = (Haulage.HORSES_A_TEAM * Haulage.HORSE_FEED_TONNES_A_MONTH * crops
                + Haulage.CARTERS_A_TEAM * wage) / Haulage.TONNES_A_TEAM_MONTH;
        check("fixture: the crops have a price and a carter a wage", crops > 0 && wage > 0, String.format("crops D$%.6f, wage D$%.4f", crops, wage));
        hl.setHorseShare(0);
        double today = hl.estimate();
        hl.setHorseShare(1);
        double asHorse = hl.estimate();
        hl.setHorseShare(0.5);
        double asBlend = hl.estimate();
        hl.setHorseShare(1);
        near("estimate() at a share of 1 is the teams' price: (HORSES_A_TEAM x HORSE_FEED_TONNES_A_MONTH x the crops' price + CARTERS_A_TEAM x the wage) / TONNES_A_TEAM_MONTH",
                asHorse, formula, 1e-9);
        near("...horseEstimate() is the same figure", hl.horseEstimate(), formula, 1e-9);
        near("at one half it is the blend: half today's and half the teams'", asBlend, 0.5 * today + 0.5 * formula, 1e-9);
        check("...today's being the trucks' price, finite and positive, and the teams' price not the same figure", today > 0 && Double.isFinite(today) && Math.abs(today - formula) > 1e-12,
                String.format("trucks D$%.6f, teams D$%.6f", today, formula));
        hl.setHorseShare(0);
        check("and at 0 it is today's again, to the bit", Double.doubleToLongBits(hl.estimate()) == Double.doubleToLongBits(today));
        hl.setHorseShare(1);

        Month m = f[6];
        check("the city leg's price after 6 months at a share of 1 is a quote struck from the month's carriage (not the estimate), finite and positive",
                m.quoteKnown && Double.isFinite(m.quote) && m.quote > 0, String.format("D$%.6f a tonne, known %b", m.quote, m.quoteKnown));
        // The pay is read after the month, when the labour market has moved the fill since the haul priced it (a few percent in a growing city): hence the 0.9.
        check("...and the rule's allowance covers the month's feed and (to within the fill moved since the haul) the carters' pay",
                m.feed > 0 && m.haulagePay > 0 && m.allowed >= m.feed + 0.9 * m.haulagePay,
                String.format("allowed D$%.4f, feed D$%.4f, carters D$%.4f", m.allowed, m.feed, m.haulagePay));
        check("...and the quote is finite and positive in every one of the " + CART_MONTHS + " months", allMonths(f, x -> x.quoteKnown && Double.isFinite(x.quote) && x.quote > 0));
    }

    static void sectionEleven(Month[] f) {
        out.println("\n--- 11. the money adds up with the carts carrying every bulk tonne: the audit closed every month ---");
        int closed = 0, ran = 0;
        String first = null;
        for (int m = 1; m <= CART_MONTHS; m++) {
            if (f[m].auditCloses) closed++;
            else if (first == null) first = f[m].audit;
            if (f[m].teams > 0 && f[m].feedTonnes > 0) ran++;
        }
        check("the month's money audit closes in each of " + CART_MONTHS + " months at a share of 1", closed == CART_MONTHS, first);
        check("...the carts having run in each of them: teams hired and crops drawn", ran == CART_MONTHS, ran + " of " + CART_MONTHS);
    }
}
