package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck
 * book's prices, what each class is billed at them, the coal the plants burn bought from the market, the utility's margin and the
 * money audit. PowerDispatchCheck proves the dispatch on bare rows; this proves the money, on real cities.
 *
 * THE TIMING, which every section reads by. The dispatch at the end of month t-1 is struck in month t's updateEconomy
 * (UtilitiesHandler.strikePower()); its ratio is month t's, its prices are what the statements are struck at at the top of month
 * t+1, and its coal is drawn from the market at that strike and paid in t+1's servicesNet. So a harness reads the book a statement is
 * struck at, and the draw and ratio the sector holds, BEFORE the month that strikes it runs (Step), and the statement after it.
 *
 * What this has to prove:
 *   1. THE STRUCK PRICE IS THE DISPATCH'S: commerce pays (energy + transmission + distribution) x month's hours, industry
 *      (energy + transmission), and the energy price is the half-blocks' prices weighted by the energy each served, over the
 *      energy served, over (1 - losses), recomputed from the book's own price[], load[] and unserved[].
 *   2. EACH CLASS'S BILL IS ITS PRICE ON WHAT WAS DELIVERED, EXACTLY: a commerce sector's statement is draw x ratio x the
 *      commerce price, a Mining sector's (every building high-voltage) draw x ratio x the industry price; industry pays no
 *      distribution.
 *   3. A SHORT HALF-BLOCK BILLS THE CAP: on a city asking twice the plant, every short half-block carries the struck VOLL, and
 *      the month's ratio is the energy served over the energy asked.
 *   4. A FUEL SHOCK MOVES THE NEXT MONTH'S BILLS: two copies of one city, one with the coal's price tripled in the dispatch: its
 *      coal row's marginal, its energy price, its struck prices and then its commerce bills are higher.
 *   5. THE UTILITY'S MARGIN IS THE TREASURY'S LINE: income = revenue - payroll - power bought - fuel bill, and the services' net
 *      the treasury takes carries it.
 *   6. THE FUEL'S MONEY REACHES ITS SELLERS: the coal the book burned is what the market drew for the city, what Coal Mining
 *      was paid is fuelLocal, what the world was paid fuelImported, and the upkeep is the book's at the exchange rate.
 *   7. THE MONEY AUDIT CLOSES over every month of every fixture, with the coal bought at home and abroad, and the two TRADE
 *      lines (utilities FuelImports, utilities PlantUpkeep) carry the imports and the upkeep.
 *
 * Every expected figure is the model's own constant or getter; every fixture CAUSES its condition: the founding grid is free
 * (UtilitiesHandler.BASE_GRID_KW, a fuelled row at marginal 0) and a town it covers clears at nothing, so the homes' draw is raised
 * as MoneyCheck does, and the fixture asserts the coal row did burn before a section reads a price.
 */
public class PowerPriceCheck {

    static int fails = 0, checks = 0, sectionStart = 0;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    /** How many homes a town stands on: enough that their draw outruns the founding grid. */
    static final int HOMES = 400;

    /** Each home's draw in a town, kW: MoneyCheck's and WoodProductsCheck's lever, so 400 homes outrun the founding grid and the coal plant is the margin. */
    static final double HOME_KW = 50;

    /** How many times the coal plant's production the short town's homes ask of the grid: past the plant and the founding grid in every season. */
    static final double SHORT_ASK = 2;

    /** How many times the coal's price the shocked city's dispatch is told. */
    static final double SHOCK = 3;

    /** The months a town is played before a section reads it: the plants are staffed and the mine is dug and stocked. */
    static final int WARM = 8;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        checks++;
        if (!ok) fails++;
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    /** Whether two figures agree to tol of the larger. */
    static boolean near(double a, double b, double tol) {
        return a == b || Math.abs(a - b) <= tol * Math.max(Math.abs(a), Math.abs(b));
    }

    static String f(double v) { return String.format("%.9g", v); }

    static void section(String title) {
        System.out.println("\n--- " + title + " ---");
        sectionStart = checks;
    }

    static void sectionEnd() {
        System.out.println("  (" + (checks - sectionStart) + " checks)");
    }

    static void quietly(Runnable work) {
        PrintStream out = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(out); }
    }

    /* =====================================================================
       THE FIXTURES
       ===================================================================== */

    /** One month run: what the strike at its top read before it ran, and what stood after it. */
    static final class Step {
        /** The month before it ran. */
        int month;
        /** The book its statements were struck at (struck in the month before), and the book it struck. */
        UtilitiesHandler.Book book, struckAfter;
        /** Per sector key before the month: draw, ratio, high-voltage share, commerce price, industry price. */
        final Map<String, double[]> held = new LinkedHashMap<>();
        /** Per sector key after the month: the electricity its statement struck. */
        final Map<String, Double> bills = new LinkedHashMap<>();
        /** The coal Coal Mining held before the month. */
        double coalHeld;
        /** After: the fuel paid, the margin's terms, the treasury's line. */
        double fuelLocal, fuelImported, upkeep, revenue, payroll, bought, income, waterIncome, net, treasury, charges;
        /** After: the coal drawn for the city this month: units from Coal Mining, units from the world, what each cost. */
        double localUnits, importedUnits, localCost, importedCost;
        /** After: the exchange rate, Coal Mining's statement's coal sold at home, the coal row of the dispatch the month ended on. */
        double exchange, minePaidAtHome, latestEnergyPrice, latestCoalMarginal, latestCoalKwh;
        MoneyAudit.Result audit;
    }

    static final class City {
        final String label;
        final Game g;
        final UtilitiesHandler u;
        final boolean mine;
        final List<Step> steps = new ArrayList<>();

        City(String label, Game g, boolean mine) {
            this.label = label;
            this.g = g;
            this.u = g.getServicesManager().getUtilitiesHandler();
            this.mine = mine;
        }

        Step last() { return steps.get(steps.size() - 1); }
    }

    static final List<MoneyAudit.Result> audits = new ArrayList<>();
    static final List<String> auditCities = new ArrayList<>();

    /**
     * A town: founded, the Wealthy preset's cash and ground, homes, shops, a coal plant, water and roads (MaterialsCheck.town()'s
     * list), standing at once (buildStack(..., noConstruction): LongPlaytest.build() orders real construction, and a town with no
     * depot waits on its builders for ever). `ask` homes ask that many times the coal plant's production; otherwise each draws
     * HOME_KW. `mine` stands a Surface Coal Mine on a thermal site listed by fiat.
     */
    static City town(String label, boolean mine, double ask) {
        Game g = new Game(GameFiles.scratch("powerprice-" + label));
        StringBuilder failed = new StringBuilder();
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            LandManager land = g.getLandManager();
            land.setOwnedSqFt(land.getOwnedSqFt() + 400_000_000L);
            BuildingManager b = g.getBuildingManager();
            double kw = ask > 0 ? ask * b.getTemplateByName("Coal Power Plant").getProduction1() / HOMES : HOME_KW;
            b.getTemplateByName("House").setElectricityConsumption((int) Math.ceil(kw));
            if (mine) {
                land.restoreSites(Resource.COAL, land.getSites(Resource.COAL) + 1,
                        land.getOwnedAmount(Resource.COAL) + Resource.COAL.amountPerSite());
            }
            String[][] orders = { { "House", "" + HOMES }, { "Small Grocery Store", "4" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
                    { "Paved Road", "10" }, { "Surface Coal Mine", "1" } };
            for (String[] o : orders) {
                if (o[0].equals("Surface Coal Mine") && !mine) continue;
                // ...standing at once (noConstruction): no depot, no builders, no months waiting on either.
                Game.BuildResult r = g.buildStack(b.getTemplateByName(o[0]), Integer.parseInt(o[1]), true);
                if (r != Game.BuildResult.SUCCESS) failed.append(o[0]).append(' ').append(r).append("; ");
            }
        });
        check("fixture: the " + label + " town's orders all stand", failed.length() == 0, "refused: " + failed);
        return new City(label, g, mine);
    }

    /** What the city drew of a good from the market this month, by the draw log the clearing kept with the month's fills: {units local, units imported, cost local, cost imported}. */
    static double[] cityDraw(Game g, Good good) {
        double[] d = new double[4];
        for (Trade t : g.getMarkets().get(good).getTrades()) {
            if (!Trade.CITY.equals(t.buyer())) continue;
            int k = t.isImport() ? 1 : 0;
            d[k] += t.units();
            d[2 + k] += t.value();
        }
        return d;
    }

    static PowerDispatch.Row coalRow(UtilitiesHandler u) {
        for (PowerDispatch.Row r : u.getLatestRows()) if (r.fuel == Good.THERMAL_COAL) return r;
        return null;
    }

    /** Runs one month of a city and keeps what the harness reads: the strike's book and the sectors' draws before, the statements and the fuel's money after. */
    static Step run(City c) {
        Game g = c.g;
        UtilitiesHandler u = c.u;
        Step st = new Step();
        st.month = g.getMonth();
        st.book = u.getStruckBook();
        for (Sector s : g.getSectors().all()) {
            st.held.put(s.key(), new double[] { s.electricity, s.getEnergyRatio(), s.getHighVoltageShare(), s.pricePerWatt, s.pricePerWattIndustry });
        }
        Sector coal = g.getSectors().byKey(Sectors.COAL_MINING);
        st.coalHeld = coal.getStock(Good.THERMAL_COAL);
        quietly(() -> g.simulateMonths(1));
        for (Sector s : g.getSectors().all()) st.bills.put(s.key(), s.statement().electricity);
        st.struckAfter = u.getStruckBook();
        st.fuelLocal = u.getFuelLocal();
        st.fuelImported = u.getFuelImported();
        st.upkeep = u.getUpkeepPaid();
        st.revenue = u.getElectricityRevenue();
        st.payroll = u.getElectricityPayroll();
        st.bought = u.getPowerBought();
        st.income = u.getElectricityIncome();
        st.waterIncome = u.getWaterIncome();
        st.net = g.getServicesManager().getServiceNetIncome();
        st.treasury = g.getEconomyManager().getUtilityIncome();
        st.charges = g.getEconomyManager().getSectorElectricityCharges();
        double[] d = cityDraw(g, Good.THERMAL_COAL);
        st.localUnits = d[0]; st.importedUnits = d[1]; st.localCost = d[2]; st.importedCost = d[3];
        st.exchange = g.getMarkets().getExchangeRate();
        st.minePaidAtHome = coal.statement().sold.containsKey(Good.THERMAL_COAL) ? coal.statement().sold.get(Good.THERMAL_COAL).atHome : 0;
        st.latestEnergyPrice = u.getLatestMonth().energyPrice;
        PowerDispatch.Row row = coalRow(u);
        st.latestCoalMarginal = row == null ? 0 : row.marginal;
        st.latestCoalKwh = row == null ? 0 : row.kwh;
        st.audit = g.getLastMoneyAudit();
        audits.add(st.audit);
        auditCities.add(c.label + " m" + st.month);
        c.steps.add(st);
        System.out.printf("   %s m%d: ratio %.4f, energy %s, coal burned %.1f t (held %.1f), bought %.1f t home / %.1f t abroad, fuel %s + %s + upkeep %s, audit residual %s%n",
                c.label, st.month, st.struckAfter.ratio, f(st.struckAfter.energyPrice), st.struckAfter.fuel.getOrDefault("THERMAL_COAL", 0.0), st.coalHeld,
                st.localUnits, st.importedUnits, f(st.fuelLocal), f(st.fuelImported), f(st.upkeep), f(st.audit.residual));
        return st;
    }

    static City play(City c, int months) {
        for (int i = 0; i < months; i++) run(c);
        return c;
    }

    /** The unit the city's money constants are struck at: Game.restrikeMoneyConstants()'s own, the denomination's unit over the struck level. */
    static double unitOf(Game g) {
        double unit = g.getDenomination().getUnit();
        double level = g.getExpectations().getStruckLevel();
        if (!(level > 0)) level = 1;
        return (unit > 0 ? unit : 1) / level;
    }

    /* =====================================================================
       MAIN
       ===================================================================== */

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        System.out.println("=== PowerPriceCheck: the bills, the fuel's money and the audit, through the game's own month (PW1) ===");
        City mine = town("mine", true, 0);
        play(mine, WARM);
        fixture(mine);
        struckPrice(mine);
        classBills(mine);
        shortCity();
        fuelShock();
        margin(mine);
        fuelMoney(mine);
        auditCloses(mine);
        System.out.println(fails == 0 ? "PowerPriceCheck: all ok (" + checks + " checks)" : "PowerPriceCheck: " + fails + " FAILED of " + checks);
        System.exit(fails);
    }

    /* ---------- the fixture, before any section ---------- */

    static void fixture(City c) {
        section("0. the fixture: a town that burns coal, a mine that is dug and stocked");
        UtilitiesHandler.Book b = c.u.getStruckBook();
        Sector coal = c.g.getSectors().byKey(Sectors.COAL_MINING);
        check("the coal row burned: the struck book's THERMAL_COAL is positive (the founding grid does not cover the town)",
                b.fuel.getOrDefault("THERMAL_COAL", 0.0) > 0, b.fuel + "");
        check("the plant made fuelled energy and the energy price is positive", b.fuelledKwh > 0 && b.energyPrice > 0, "fuelled " + b.fuelledKwh + " kWh, energy " + b.energyPrice);
        check("the month is served: no half-block short, ratio 1", b.ratio == 1, "ratio " + b.ratio);
        check("Coal Mining stands, is staffed and is stocked with coal", c.g.getBuildingManager().totalBySector(Sectors.COAL_MINING, t -> 1) >= 1
                && coal.getStock(Good.THERMAL_COAL) > 0, "stock " + coal.getStock(Good.THERMAL_COAL));
        sectionEnd();
    }

    /* ---------- 1 ---------- */

    /** The energy price recomputed from the book's own half-blocks: the prices weighted by the energy each served, over the energy served, over (1 - losses). */
    static double energyPriceOf(UtilitiesHandler.Book b) {
        double paid = 0, served = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            double kwh = (b.load[s] - b.unserved[s]) * PowerDispatch.halfHours(s);
            paid += kwh * b.price[s];
            served += kwh;
        }
        return served > 0 ? paid / served / (1 - PowerDispatch.GRID_LOSSES) : 0;
    }

    static void pricesOfBook(String who, Game g, UtilitiesHandler.Book b) {
        double unit = unitOf(g), hours = PowerDispatch.MONTH_HOURS;
        double transmission = PowerDispatch.TRANSMISSION_A_KWH / unit, distribution = PowerDispatch.DISTRIBUTION_A_KWH / unit;
        check(who + ": commerce pays the energy and all the wires: (energy + TRANSMISSION + DISTRIBUTION) x the month's hours, at the city's unit",
                near(b.commercePrice, (b.energyPrice + transmission + distribution) * hours, 1e-12), f(b.commercePrice) + " against " + f((b.energyPrice + transmission + distribution) * hours));
        check(who + ": industry pays the energy and the transmission alone: (energy + TRANSMISSION) x the month's hours",
                near(b.industryPrice, (b.energyPrice + transmission) * hours, 1e-12), f(b.industryPrice) + " against " + f((b.energyPrice + transmission) * hours));
        check(who + ": the two differ by the distribution x the month's hours, to 1e-12 of the commerce price",
                Math.abs((b.commercePrice - b.industryPrice) - distribution * hours) <= 1e-12 * b.commercePrice, f(b.commercePrice - b.industryPrice) + " against " + f(distribution * hours));
        double energy = energyPriceOf(b);
        check(who + ": the energy price is the half-blocks' prices weighted by the energy each served, over the energy served, over (1 - GRID_LOSSES)",
                near(b.energyPrice, energy, 1e-12), f(b.energyPrice) + " against " + f(energy));
        double asked = 0, served = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            asked += b.load[s] * PowerDispatch.halfHours(s);
            served += (b.load[s] - b.unserved[s]) * PowerDispatch.halfHours(s);
        }
        check(who + ": the book's asked and served kWh are its half-blocks' load and load less unserved over their hours, and the ratio is served over asked",
                near(b.askedKwh, asked, 1e-12) && near(b.servedKwh, served, 1e-12) && near(b.ratio, served / asked, 1e-12), "ratio " + b.ratio + " against " + served / asked);
    }

    static void struckPrice(City c) {
        section("1. the struck price is the dispatch's");
        UtilitiesHandler.Book b = c.u.getStruckBook();
        pricesOfBook("the mine town", c.g, b);
        double voll = PowerDispatch.VOLL_A_KWH / unitOf(c.g);
        boolean cap = true, none = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            if (b.unserved[s] > 0) cap &= b.price[s] == voll;
            else none &= b.price[s] < voll;
        }
        check("a half-block clears at the cap only when it is left short: the served ones clear under the VOLL", cap && none);
        check("the merit order prices rise with the load: the peak half-block clears at least as high as the base half-block, and the energy price is above 0",
                b.energyPrice > 0 && b.price[PowerDispatch.HALVES - 1] >= b.price[0], f(b.price[0]) + " .. " + f(b.price[PowerDispatch.HALVES - 1]));
        sectionEnd();
    }

    /* ---------- 2 ---------- */

    static void classBills(City c) {
        section("2. each class's bill is its price on what was delivered, exactly");
        Step st = c.last();
        UtilitiesHandler.Book book = st.book;
        double unit = unitOf(c.g), hours = PowerDispatch.MONTH_HOURS, distribution = PowerDispatch.DISTRIBUTION_A_KWH / unit;

        double[] retail = st.held.get(Sectors.RETAIL);
        check("fixture: Retail draws power, is a commerce class (no high-voltage share) and holds the book's commerce price it is struck at",
                retail[0] > 0 && retail[2] == 0 && retail[3] == book.commercePrice, "draw " + retail[0] + " share " + retail[2] + " price " + f(retail[3]) + " against " + f(book.commercePrice));
        double retailBill = st.bills.get(Sectors.RETAIL);
        double want = retail[0] * retail[1] * book.commercePrice;
        check("a commerce sector's statement electricity is draw x ratio x the commerce price of the book struck the month before, to 1e-9",
                near(retailBill, want, 1e-9), f(retailBill) + " against " + f(want));

        double[] mining = st.held.get(Sectors.COAL_MINING);
        boolean allHigh = c.g.getBuildingManager().totalBySector(Sectors.COAL_MINING, t -> BuildingsTemplate.isHighVoltage(t) ? 0 : 1) == 0;
        check("fixture: Coal Mining draws power, all its buildings are high-voltage (Mining) and its share is 1, at the book's industry price",
                mining[0] > 0 && allHigh && mining[2] == 1 && mining[4] == book.industryPrice, "draw " + mining[0] + " share " + mining[2] + " price " + f(mining[4]) + " against " + f(book.industryPrice));
        double mineBill = st.bills.get(Sectors.COAL_MINING);
        double wantMine = mining[0] * mining[1] * book.industryPrice;
        check("a Mining sector's statement electricity is draw x ratio x the industry price, to 1e-9",
                near(mineBill, wantMine, 1e-9), f(mineBill) + " against " + f(wantMine));
        check("industry pays transmission and not distribution: its price per kW less the commerce price is -distribution x the month's hours",
                Math.abs((book.industryPrice - book.commercePrice) + distribution * hours) <= 1e-12 * book.commercePrice, f(book.industryPrice - book.commercePrice) + " against " + f(-distribution * hours));
        double perKw = mineBill / (mining[0] * mining[1]), perKwRetail = retailBill / (retail[0] * retail[1]);
        check("...and read off the two statements: the Mining bill per kW delivered less the Retail bill per kW delivered is the same difference, to 1e-9 of the commerce price",
                Math.abs((perKw - perKwRetail) + distribution * hours) <= 1e-9 * book.commercePrice, f(perKw - perKwRetail));
        check("the sectors hold the ratio of the month they were struck in: the book's, which the sectors' energy ratio is",
                retail[1] == book.ratio && mining[1] == book.ratio, "retail " + retail[1] + " mining " + mining[1] + " book " + book.ratio);
        sectionEnd();
    }

    /* ---------- 3 ---------- */

    static void shortCity() {
        section("3. a short half-block bills the cap");
        City c = play(town("short", false, SHORT_ASK), WARM);
        UtilitiesHandler.Book b = c.u.getStruckBook();
        double voll = PowerDispatch.VOLL_A_KWH / unitOf(c.g);
        int short_ = 0;
        boolean cap = true, ok = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            if (b.unserved[s] > 0) { short_++; cap &= b.price[s] == voll; }
        }
        check("fixture: the homes ask " + SHORT_ASK + " times the plant: the plant burned coal and at least one half-block is left short",
                b.fuel.getOrDefault("THERMAL_COAL", 0.0) > 0 && short_ > 0, short_ + " short half-blocks");
        check("fixture: the short month is not served whole: its ratio is under 1", b.ratio < 1, "ratio " + b.ratio);
        check("every short half-block carries the struck VOLL (VOLL_A_KWH over the city's unit)", cap, "voll " + f(voll));
        for (int s = 0; s < PowerDispatch.HALVES; s++) if (b.unserved[s] == 0 && b.price[s] >= voll) ok = false;
        check("...and no half-block served whole does", ok);
        double asked = 0, served = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            asked += b.load[s] * PowerDispatch.halfHours(s);
            served += (b.load[s] - b.unserved[s]) * PowerDispatch.halfHours(s);
        }
        check("the month's ratio is the energy served over the energy asked", near(b.ratio, served / asked, 1e-12) && near(b.ratio, b.servedKwh / b.askedKwh, 1e-12),
                b.ratio + " against " + served / asked);
        pricesOfBook("the short town", c.g, b);
        double shortServed = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) if (b.unserved[s] > 0) shortServed += (b.load[s] - b.unserved[s]) * PowerDispatch.halfHours(s);
        check("a short half-block's cap is in the bill: what the energy price collects on the energy served is at least the cap on what the short half-blocks served",
                b.energyPrice * (1 - PowerDispatch.GRID_LOSSES) * b.servedKwh >= voll * shortServed * (1 - 1e-12), f(b.energyPrice * (1 - PowerDispatch.GRID_LOSSES) * b.servedKwh) + " against " + f(voll * shortServed));
        Step st = c.last();
        double[] retail = st.held.get(Sectors.RETAIL);
        double want = retail[0] * retail[1] * st.book.commercePrice;
        check("a commerce sector in the short town is billed draw x ratio x the commerce price on what was delivered, and the ratio is under 1",
                retail[1] < 1 && near(st.bills.get(Sectors.RETAIL), want, 1e-9), "ratio " + retail[1]);
        sectionEnd();
    }

    /* ---------- 4 ---------- */

    static void fuelShock() {
        section("4. a fuel shock moves the next month's bills");
        City base = play(town("twin", false, 0), WARM), shock = play(town("shock", false, 0), WARM);
        UtilitiesHandler.Book a = base.u.getStruckBook(), s = shock.u.getStruckBook();
        check("fixture: the two copies are the same city before the shock: the same book, the same cash, the same month",
                near(a.commercePrice, s.commercePrice, 1e-12) && near(a.energyPrice, s.energyPrice, 1e-12) && near(base.g.getCash(), shock.g.getCash(), 1e-12)
                        && base.g.getMonth() == shock.g.getMonth(), f(a.commercePrice) + " and " + f(s.commercePrice));
        check("fixture: the coal burned in both", a.fuel.getOrDefault("THERMAL_COAL", 0.0) > 0 && s.fuel.getOrDefault("THERMAL_COAL", 0.0) > 0);

        // The lever: the coal's price in force (the game's price source is private and the world market re-sets the markets' own prices at the top of every
        // month, so it is backed out of a dispatch now: the row's marginal less the upkeep at the exchange rate, over the fuel a kWh), then held fixed in
        // BOTH copies, the shocked one told SHOCK times it. The only difference between the cities is the shock.
        BuildingsTemplate plant = base.g.getBuildingManager().getTemplateByName("Coal Power Plant");
        base.u.updateEnergyRatio();
        shock.u.updateEnergyRatio();
        PowerDispatch.Row now = coalRow(base.u);
        final double p0 = now == null ? 0 : (now.marginal - plant.getVariableUpkeep() / 1e6 * PriceLevel.FACTOR * base.g.getMarkets().getExchangeRate()) / plant.fuelPerKwh();
        check("fixture: the coal's price in force is positive, and the two copies' dispatch agree on it", p0 > 0 && near(coalRow(shock.u).marginal, now == null ? 0 : now.marginal, 1e-12), f(p0));
        base.g.getServicesManager().setFuelPrice(good -> p0);
        shock.g.getServicesManager().setFuelPrice(good -> SHOCK * p0);

        Step b1 = run(base), s1 = run(shock);
        PowerDispatch.Row br = coalRow(base.u), sr = coalRow(shock.u);
        check("fixture: both cities' dispatch ran the coal row", br != null && sr != null && br.kwh > 0 && sr.kwh > 0);
        check("the shocked city's coal row has the higher marginal", sr != null && br != null && sr.marginal > br.marginal, f(s1.latestCoalMarginal) + " against " + f(b1.latestCoalMarginal));
        check("...the energy it ran is the same: the shock moves the price and not the order", near(s1.latestCoalKwh, b1.latestCoalKwh, 1e-12));
        check("the shocked city's dispatch month has the higher energy price", s1.latestEnergyPrice > b1.latestEnergyPrice, f(s1.latestEnergyPrice) + " against " + f(b1.latestEnergyPrice));

        Step b2 = run(base), s2 = run(shock);
        check("the month after, the shocked city's struck book has the higher energy price, and it is the dispatch month's",
                s2.struckAfter.energyPrice > b2.struckAfter.energyPrice && near(s2.struckAfter.energyPrice, s1.latestEnergyPrice, 1e-12),
                f(s2.struckAfter.energyPrice) + " against " + f(b2.struckAfter.energyPrice));
        check("...and the higher commerce and industry prices",
                s2.struckAfter.commercePrice > b2.struckAfter.commercePrice && s2.struckAfter.industryPrice > b2.struckAfter.industryPrice);
        check("...while the bills struck that month are still at the book before the shock",
                near(s2.book.commercePrice, b2.book.commercePrice, 1e-12));

        Step b3 = run(base), s3 = run(shock);
        double[] hb = b3.held.get(Sectors.RETAIL), hs = s3.held.get(Sectors.RETAIL);
        check("fixture: the two cities' Retail draw and ratio are the same when the shocked prices reach the statements", hb[0] > 0 && hb[0] == hs[0] && hb[1] == hs[1], hb[0] + " and " + hs[0]);
        double bill0 = b3.bills.get(Sectors.RETAIL), bill1 = s3.bills.get(Sectors.RETAIL);
        check("the shocked city's commerce bill is the higher: its next bills carry the fuel", bill1 > bill0, f(bill1) + " against " + f(bill0));
        check("...in the ratio of the two books' commerce prices, to 1e-9",
                near(bill1 / bill0, s3.book.commercePrice / b3.book.commercePrice, 1e-9), f(bill1 / bill0) + " against " + f(s3.book.commercePrice / b3.book.commercePrice));

        // The lever to the bit, last (the exchange rate moves inside a month, and a re-dispatch moves the book the next month strikes): dispatched again
        // now, a coal row's marginal is the price it was told x the fuel a kWh, plus the upkeep at the exchange rate.
        base.u.updateEnergyRatio();
        shock.u.updateEnergyRatio();
        // PW1: this read the upkeep as variableUpkeep / 1e6 x the exchange rate, the code's own formula, so it held while the upkeep was
        // 38.6 times too dear (today's dollars, not the founding's); the row's upkeep is checked against FACTOR first now.
        check("the coal row's upkeep a kWh is the plant's US$ a MWh at the founding's price level, world money as a good's world price is struck (variableUpkeep / 1e6 x PriceLevel.FACTOR); the exchange rate carries the world's inflation since",
                coalRow(base.u) != null && near(coalRow(base.u).upkeepPerKwh, plant.getVariableUpkeep() / 1e6 * PriceLevel.FACTOR, 1e-12),
                (coalRow(base.u) == null ? "no row" : f(coalRow(base.u).upkeepPerKwh)) + " against " + f(plant.getVariableUpkeep() / 1e6 * PriceLevel.FACTOR));
        double upkeep = plant.getVariableUpkeep() / 1e6 * PriceLevel.FACTOR * base.g.getMarkets().getExchangeRate();
        double wantBase = p0 * plant.fuelPerKwh() + upkeep, wantShock = SHOCK * p0 * plant.fuelPerKwh() + upkeep;
        PowerDispatch.Row again = coalRow(base.u), againShock = coalRow(shock.u);
        check("dispatched again, the coal row's marginal is the price it was told x the fuel a kWh plus the upkeep at the exchange rate, to 1e-12",
                again != null && near(again.marginal, wantBase, 1e-12), (again == null ? "no row" : f(again.marginal)) + " against " + f(wantBase));
        check("...and the shocked city's is SHOCK times the coal's part of it, to 1e-12: the shock reaches the fuel and not the upkeep",
                againShock != null && near(againShock.marginal, wantShock, 1e-12), (againShock == null ? "no row" : f(againShock.marginal)) + " against " + f(wantShock));
        sectionEnd();
    }

    /* ---------- 5 ---------- */

    static void margin(City c) {
        section("5. the utility's margin is the treasury's line");
        Step st = c.last();
        double fuelBill = st.fuelLocal + st.fuelImported + st.upkeep;
        check("fixture: a month with coal bought: the fuel bill is positive", fuelBill > 0 && c.u.getFuelBill() == fuelBill, f(fuelBill));
        double want = st.revenue - st.payroll - st.bought - fuelBill;
        check("the electricity income is revenue less payroll less the power bought less the fuel bill, to 1e-9",
                near(st.income, want, 1e-9) || Math.abs(st.income - want) <= 1e-9 * st.revenue, f(st.income) + " against " + f(want));
        check("the revenue is what the sectors' statements charged for electricity", near(st.revenue, st.charges, 1e-12), f(st.revenue) + " against " + f(st.charges));
        check("the services' net the treasury takes is the electricity income and the water income, to 1e-9",
                Math.abs(st.net - (st.income + st.waterIncome)) <= 1e-9 * Math.max(Math.abs(st.net), st.revenue), f(st.net));
        UtilitiesHandler u = c.u;
        double local = u.getFuelLocal(), imported = u.getFuelImported(), upkeep = u.getUpkeepPaid();
        u.setFuelPaid(0, 0, 0);
        double without = c.g.getServicesManager().getServiceNetIncome();
        u.setFuelPaid(local, imported, upkeep);
        double with = c.g.getServicesManager().getServiceNetIncome();
        check("the services' net carries the fuel: with none paid it is the higher by exactly the fuel bill",
                Math.abs((without - with) - fuelBill) <= 1e-9 * Math.max(Math.abs(without), fuelBill), f(without - with) + " against " + f(fuelBill));
        check("the line the treasury took is the services' net the month's strike read",
                Math.abs(st.treasury - st.net) <= 1e-9 * Math.max(Math.abs(st.net), st.revenue), f(st.treasury) + " against " + f(st.net));
        sectionEnd();
    }

    /* ---------- 6 ---------- */

    static void fuelMoney(City c) {
        section("6. the fuel's money reaches its sellers");
        Step st = c.last();
        double burned = st.book.fuel.getOrDefault("THERMAL_COAL", 0.0);
        double drawn = st.localUnits + st.importedUnits;
        check("the coal the struck book burned is what the market drew for the city that month, to 1e-9", burned > 0 && near(drawn, burned, 1e-9), f(drawn) + " t against " + f(burned) + " t");
        check("fixture: Coal Mining was stocked, so the city bought at home", st.coalHeld > burned && st.localUnits > 0, f(st.coalHeld) + " t held");
        check("fuelLocal is what Coal Mining was paid for it: the cost of the units drawn from its stock, to 1e-9", near(st.fuelLocal, st.localCost, 1e-9), f(st.fuelLocal) + " against " + f(st.localCost));
        check("...and Coal Mining's statement booked it among its sales at home", st.minePaidAtHome >= st.fuelLocal * (1 - 1e-9), f(st.minePaidAtHome));
        check("fuelImported is what the world was paid for the rest, to 1e-9", near(st.fuelImported, st.importedCost, 1e-9) || (st.fuelImported == 0 && st.importedCost == 0), f(st.fuelImported) + " against " + f(st.importedCost));
        check("fuelLocal + fuelImported is the draw's cost", near(st.fuelLocal + st.fuelImported, st.localCost + st.importedCost, 1e-9));
        check("the upkeep is the book's variable upkeep (world money) at the exchange rate, to 1e-9",
                near(st.upkeep, st.book.upkeepWorld * st.exchange, 1e-9), f(st.upkeep) + " against " + f(st.book.upkeepWorld * st.exchange));

        // Both ways in one month: Coal Mining holds half of what the book burns, so the city buys half at home and half abroad.
        Sector coal = c.g.getSectors().byKey(Sectors.COAL_MINING);
        double want = c.u.getStruckBook().fuel.getOrDefault("THERMAL_COAL", 0.0), half = want / 2;
        coal.setStock(Good.THERMAL_COAL, half);
        Step both = run(c);
        double units = both.book.fuel.getOrDefault("THERMAL_COAL", 0.0);
        check("fixture: Coal Mining held half of the month's burn at the strike, and that month's draw is the burn, to 1e-9",
                units > 0 && near(both.coalHeld, half, 1e-9) && near(both.localUnits + both.importedUnits, units, 1e-9), f(both.localUnits) + " + " + f(both.importedUnits) + " against " + f(units));
        check("the city bought at home what Coal Mining held and the rest abroad: both are positive, and the home part is the stock",
                both.localUnits > 0 && both.importedUnits > 0 && near(both.localUnits, both.coalHeld, 1e-9) && near(both.importedUnits, units - both.coalHeld, 1e-9),
                f(both.localUnits) + " t at home, " + f(both.importedUnits) + " t abroad");
        check("...fuelLocal pays Coal Mining and fuelImported the world, each positive, and together the draw's cost",
                both.fuelLocal > 0 && both.fuelImported > 0 && near(both.fuelLocal, both.localCost, 1e-9) && near(both.fuelImported, both.importedCost, 1e-9)
                        && near(both.fuelLocal + both.fuelImported, both.localCost + both.importedCost, 1e-9), f(both.fuelLocal) + " + " + f(both.fuelImported));
        check("...and the upkeep is still the book's at the exchange rate", near(both.upkeep, both.book.upkeepWorld * both.exchange, 1e-9), f(both.upkeep));
        sectionEnd();
    }

    /* ---------- 7 ---------- */

    static void auditCloses(City c) {
        section("7. the money audit closes");
        int open = 0;
        double worst = 0;
        String worstAt = "";
        for (int i = 0; i < audits.size(); i++) {
            MoneyAudit.Result r = audits.get(i);
            double over = Math.abs(r.residual) - MoneyAudit.tolerance(r.moved());
            if (over > 0) {
                open++;
                System.out.println("  open: " + auditCities.get(i) + " " + r);
            }
            if (Math.abs(r.residual) > worst) { worst = Math.abs(r.residual); worstAt = auditCities.get(i); }
        }
        check("every month of every fixture (" + audits.size() + " months) closes: the residual is within MoneyAudit.tolerance() of what moved", open == 0 && !audits.isEmpty(),
                open + " months open; worst residual " + f(worst) + " at " + worstAt);

        Step both = c.last();
        MoneyAudit.Result r = both.audit;
        double imports = r.figure("- utilities FuelImports"), upkeep = r.figure("- utilities PlantUpkeep");
        check("fixture: the last month bought coal at home and abroad, and ran up upkeep", both.fuelLocal > 0 && both.fuelImported > 0 && both.upkeep > 0);
        check("the audit's FuelImports line is the coal bought from the world, to 1e-9", imports > 0 && near(imports, both.fuelImported, 1e-9), f(imports) + " against " + f(both.fuelImported));
        check("the audit's PlantUpkeep line is the upkeep paid abroad, to 1e-9", upkeep > 0 && near(upkeep, both.upkeep, 1e-9), f(upkeep) + " against " + f(both.upkeep));
        check("both are in TRADE: the month's imports (tradeOut) hold the two lines", r.tradeOut >= (imports + upkeep) * (1 - 1e-12), f(r.tradeOut) + " against " + f(imports + upkeep));
        check("the coal bought of the mines is a pool paying a pool: no line of its own, and the audit still closes",
                r.figure("- utilities FuelLocal") == 0 && Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved()), f(r.residual));
        sectionEnd();
    }
}
