package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The farmland reserve, the model (RG1, 2026-11-25; Jerus, 2026-11-24: "Policy, that also lets farms crops as well be cheaper"): the
 * fields' zone at the sellers' farmland price, outside the crowding premium, holding ground for every farm - Agriculture's fields and
 * the city's Wind and Solar Farms - against the ground as it was priced without it.
 *
 * WHY. REV-PW3b (item 4) found a Wind Farm's ground 87% of its capital and a Solar Farm's 96% at the going price on the default run's
 * grown city, so it built none, and the fields have run on the same clock. Zoning's WHY has the real reserves the policy is (British
 * Columbia's Agricultural Land Reserve, Oregon's Exclusive Farm Use zones, Ontario's Greenbelt); this harness causes both paths.
 *
 * What this has to prove:
 *   1. THE FARMLAND PRICE is the sellers' own figure: Canada's published farmland a hectare (LandFactors' table) carried to 2025's
 *      dollars over the base, a figure inside real farmland's (US pasture to cropland, LandMarket's THE CROWDING PREMIUM), times the
 *      host's land factor; and it stands outside the crowding premium - a crowded city's going price rises, the farmland price does not.
 *   2. WITHOUT THE POLICY, TODAY'S PRICES: no zone, a field's ground at the going price and a Wind Farm on its own key; the fields'
 *      zone at a share sells at that share, holds nothing for a Wind Farm, and the planner's capital is the ground as before.
 *   3. WITH IT, A FARM'S GROUND COSTS THE FARMLAND PRICE: the reserve's share is the farmland price over the going price, its flat
 *      ground's price the farmland price; the dial is kept and comes back when the reserve is lifted; only the fields' zone may be the
 *      reserve, and the reserve never sells above the going price.
 *   4. THE MONEY: a field bought in the reserve through the businesses' door pays the farmland price, the city has exactly that, and
 *      the month's money audit closes: no new payer, no new payee.
 *   5. THE CITY'S OWN FARMS: a Wind Farm the city builds takes the reserve's ground, which it uses as the fields would, and gives it
 *      back when it is demolished; with no reserve it leaves the fields' zone alone.
 *   6. THE PLANNER BUILDS FARMS WHERE THE DEFAULT BUILT NONE: PowerPlanCheck 6's dear town orders wind on its ground; crowded, a Wind
 *      Farm's ground is most of its capital and it orders none - nor with the fields' zone at a share; with the reserve the farm's
 *      ground is at the farmland price, a sliver of its capital, and it orders wind again. A Solar Farm's capital the same.
 *   7. THE SAVE: a city with the reserve saved and loaded is the same city, a month on too; an older save's zone, which has no
 *      reserve key, reads as a zone at its share.
 */
public final class FarmlandReserveCheck {
    static PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
    static int fails;

    static void check(String label, boolean ok) {
        out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static boolean close(double a, double b, double tol) {
        return Math.abs(a - b) <= tol * Math.max(1e-12, Math.max(Math.abs(a), Math.abs(b)));
    }

    static void quietly(Runnable r) {
        PrintStream before = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(before); }
    }

    static final String FIELDS = Zoning.RESERVE_SECTOR;

    /** Real ground a square foot, US$ (LandMarket's THE CROWDING PREMIUM): US pasture, and cropland - what a farm's ground costs. */
    static final double PASTURE_USD = 0.044, CROPLAND_USD = 0.134;

    /** The base's dollars a square foot today (LandMarket.BASE_PRICE_PER_SQ_FT reads .0007 in thousands, at the founding's level). */
    static double baseUsdToday() { return LandMarket.openingUsdPerSqFt() / PriceLevel.FACTOR * 1000; }

    /** The reserve the fixtures zone: square kilometres of the fields' zone, enough for a Solar Farm's ground and more. */
    static final double RESERVE_KM2 = 2;

    /** The dial the fields' zone is set at before it is the reserve: a share, to tell the two prices apart. */
    static final double DIAL = 0.5;

    /**
     * ZoningCheck.city()'s fixture with no months played: a founded scratch city, the defaults put back, the rate pinned, cash and
     * 200 million square feet more ground, the fields held out of the investment engine (they are bought by hand here), and the
     * Wind Farm known (EssentialWorksCheck.knowWind()) so the city may build one.
     */
    static Game city(Path root, String name) {
        Game g = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")));
        quietly(() -> {
            g.run();
            g.getEconomyManager().getTaxPolicy().reset();
            g.getForeignAccounts().pinRate(g.getForeignAccounts().getRate());
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            g.getBusinessInvestment().holdSector(Sectors.AGRICULTURE);
            EssentialWorksCheck.knowWind(g);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        Path root = TempDirs.create("farmlandcheck");
        thePrice(root);
        withoutIt(root);
        withIt();
        Game g = city(root, "city");
        theMoney(g);
        theCitysFarms(g);
        thePlanner(root);
        theSave(root, g);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE FARMLAND PRICE ============================ */

    static void thePrice(Path root) {
        out.println("--- 1. the farmland price is the sellers' own figure, outside the crowding premium ---");
        double usdHa = LandFactors.referenceUsdPerHectare();
        double usdSqFtToday = usdHa * PriceLevel.usCpi(PriceLevel.LAST_YEAR) / PriceLevel.usCpi(LandFactors.BASE_YEAR)
                / (LandManager.SQ_FT_PER_KM2 / 100);
        check(String.format("Canada's farmland a hectare is the table's (US$%,.2f of %d), $%.3f a square foot in %d's dollars: %.3f of the base's $%.2f",
                        usdHa, LandFactors.BASE_YEAR, usdSqFtToday, PriceLevel.LAST_YEAR, LandMarket.farmlandOfBase(), baseUsdToday()),
                close(LandMarket.farmlandOfBase(), usdSqFtToday / baseUsdToday(), 1e-9));
        check(String.format("...a farm's price: between US pasture's $%.3f and cropland's $%.3f a square foot", PASTURE_USD, CROPLAND_USD),
                usdSqFtToday > PASTURE_USD && usdSqFtToday < CROPLAND_USD);

        Game g = city(root, "price");
        LandManager land = g.getLandManager();
        LandMarket m = land.getMarket();
        double farm = land.getFarmlandPricePerSqFt(), going = land.getPricePerSqFt();
        check(String.format("the city's farmland price is the anchor (%.3g) x that x its host's land factor (%.3f): %.3g a square foot, the going price %.3g",
                        m.getBasePricePerSqFt(), m.hostLandFactor(), farm, going),
                farm > 0 && close(farm, m.getBasePricePerSqFt() * LandMarket.farmlandOfBase() * m.hostLandFactor(), 1e-12));
        double premium0 = land.getCrowdingPremium();
        long crowd = (long) (2 * LandMarket.CROWDING_MIDPOINT * land.getOwnedSqFt() / LandManager.SQ_FT_PER_KM2);
        quietly(() -> land.updateMarket(crowd));
        check(String.format("crowded to %,.0f a km2, the going price rises %.0f-fold with the premium (%.2f to %.1f); the farmland price does not move",
                        land.getCrowding(), land.getPricePerSqFt() / going, premium0, land.getCrowdingPremium()),
                land.getCrowdingPremium() > LandMarket.CROWDING_CEILING / 2 && land.getPricePerSqFt() > 10 * going
                        && land.getFarmlandPricePerSqFt() == farm);
        check("...and the farmland share struck with it is the farmland price over the new going price",
                close(land.getZoning().farmlandShare(), farm / land.getPricePerSqFt(), 1e-12));
    }

    /* ============================ 2. WITHOUT IT ============================ */

    static void withoutIt(Path root) {
        out.println("--- 2. without the policy, today's prices ---");
        Game g = city(root, "without");
        LandManager land = g.getLandManager();
        BuildingsTemplate field = LongPlaytest.template(g, "Mixed Farm"), wind = g.getBuildingManager().getTemplateByName("Wind Farm");
        double sq = field.getLandSqFt(), going = land.getPricePerSqFt();
        double plot = land.priceFor(sq, "", LandManager.Use.BUILDING);
        check(String.format("no zone: a %s's ground (%s) costs what any building's does, %s", field.getName(), LandManager.areaWords(sq),
                        Formats.INSTANCE.cash(plot)),
                land.priceFor(sq, FIELDS, LandManager.Use.BUILDING) == plot && land.groundPriceFor(FIELDS) == going);
        check("...and a Wind Farm's ground is taken under its own key, the city's, no reserve's",
                land.reserveKeyOf(wind) == null && wind.getSector().equals(land.groundKeyOf(wind)));
        double left = land.getAvailableSqFt();
        check("...the planner's capital for it is the quote and the ground at landValue(), as before",
                BuildAdvice.capitalOf(g, wind, 1, left) == g.quoteBuild(wind, 1).total + BuildAdvice.landValue(g, wind.getLandSqFt(), left));

        quietly(() -> g.setZone(FIELDS, Zoning.Size.KM2, RESERVE_KM2, DIAL));
        check(String.format("the fields' zone at %.0f%% of the going price: their ground sells at that share", 100 * DIAL),
                close(land.groundPriceFor(FIELDS), DIAL * going, 1e-12)
                        && close(land.priceFor(sq, FIELDS, LandManager.Use.BUILDING), DIAL * plot, 1e-9));
        double held = land.heldBy(FIELDS);
        check(String.format("...a zone that is not the reserve holds its %s for the fields alone: a Wind Farm's key and capital are as before",
                        LandManager.areaWords(held)),
                held > wind.getLandSqFt() && land.reserveKeyOf(wind) == null && g.getZoning().reserve() == null
                        && BuildAdvice.capitalOf(g, wind, 1, left) == g.quoteBuild(wind, 1).total + BuildAdvice.landValue(g, wind.getLandSqFt(), left));
    }

    /* ============================ 3. WITH IT ============================ */

    static void withIt() {
        out.println("--- 3. with the policy, a farm's ground costs the farmland price ---");
        Zoning z = new Zoning();
        z.strikeFarmland(0.2);
        z.set(FIELDS, Zoning.Size.KM2, RESERVE_KM2, DIAL);
        check("the fields' zone at its dial is no reserve and sells at its dial", z.reserve() == null && z.priceShare(FIELDS) == DIAL);
        Zoning.Zone r = z.setFarmland(FIELDS, true);
        check("at the farmland price it is the reserve, and sells at the farmland share struck (0.2), its dial kept",
                z.reserve() == r && r.farmland() && r.priceShare() == 0.2 && r.dialShare() == DIAL && r.size() == RESERVE_KM2);
        z.strikeFarmland(0.05);
        check("...struck again with the going price, it sells at the new share", z.reserve().priceShare() == 0.05);
        z.set(FIELDS, Zoning.Size.SHARE, 0.03, 0.8);
        check("...re-sized and re-dialled it stays the reserve, at the farmland share", z.reserve() != null
                && z.reserve().priceShare() == 0.05 && z.reserve().dialShare() == 0.8 && z.reserve().kind() == Zoning.Size.SHARE);
        z.strikeFarmland(2.5);
        check("never dearer: where the farmland price passes the going price, the reserve sells at the going price",
                z.reserve().priceShare() == Zoning.MAX_PRICE_SHARE);
        z.set(Sectors.MINING, Zoning.Size.KM2, 1, DIAL);
        check("only the fields' zone may be the reserve: a mine's zone may not", z.setFarmland(Sectors.MINING, true) == null
                && !z.zone(Sectors.MINING).farmland() && z.setFarmland(Sectors.RESTAURANTS, true) == null);
        z.setFarmland(FIELDS, false);
        check("lifted, the fields' zone is at its dial again", z.reserve() == null && z.priceShare(FIELDS) == 0.8);
        Zoning copy = z.copy();
        z.setFarmland(FIELDS, true);
        check("a copy (a preview's) is detached, and carries the farmland share struck",
                copy.reserve() == null && copy.farmlandShare() == z.farmlandShare());
    }

    /* ============================ 4. THE MONEY ============================ */

    static void theMoney(Game g) {
        out.println("--- 4. the money: a field in the reserve pays the farmland price, to the city, and the audit closes ---");
        LandManager land = g.getLandManager();
        quietly(() -> {
            g.setZone(FIELDS, Zoning.Size.KM2, RESERVE_KM2, DIAL);
            g.setFarmlandReserve(true);
        });
        double going = land.getPricePerSqFt(), farm = land.getFarmlandPricePerSqFt();
        check(String.format("fixture: the reserve stands, %s held; the farmland price %.3g a square foot, the going %.3g",
                        LandManager.areaWords(land.heldBy(FIELDS)), farm, going),
                g.getZoning().reserve() != null && farm < going && land.heldBy(FIELDS) > 0);
        check("its flat ground sells at the farmland price, its share the farmland price over the going price",
                close(land.groundPriceFor(FIELDS), farm, 1e-12) && close(g.getZoning().priceShare(FIELDS), farm / going, 1e-12));
        BuildingsTemplate field = LongPlaytest.template(g, "Mixed Farm");
        double sq = field.getLandSqFt();
        double plot = land.priceFor(sq, "", LandManager.Use.BUILDING), quoted = land.priceFor(sq, FIELDS, LandManager.Use.BUILDING);
        check(String.format("a %s's ground in the reserve is quoted at %s, the farmland share of the %s any building pays",
                        field.getName(), Formats.INSTANCE.cash(quoted), Formats.INSTANCE.cash(plot)),
                sq <= land.heldBy(FIELDS) && close(quoted, plot * farm / going, 1e-9));
        g.getSectors().byKey(FIELDS).addCash(1_000 * Math.max(1, g.quoteBuild(field, 1).total));
        double cash = g.getCash(), sales = land.getLandSalesThisMonth(), used = land.usedBy(FIELDS);
        double till = g.getSectors().byKey(FIELDS).getCash(), quote = g.quoteBuild(field, 1).total;
        boolean built = g.buildFor(g.getSectorInvestor(FIELDS), field, 1);
        check("bought through the businesses' door, the city has exactly that price, on its land sales",
                built && close(g.getCash() - cash, quoted, 1e-9) && close(land.getLandSalesThisMonth() - sales, quoted, 1e-9));
        check("...which the fields paid with their building, and the reserve's ground is used by it",
                close(till - g.getSectors().byKey(FIELDS).getCash(), quote + quoted, 1e-9) && land.usedBy(FIELDS) - used == sq);
        quietly(g::toggleNextMonth);
        MoneyAudit.Result a = g.getLastMoneyAudit();
        check(String.format("the month after, the money audit closes (residual %.3g against a tolerance of %.3g): no new payer, no new payee",
                        a.residual, MoneyAudit.tolerance(a.moved())),
                Math.abs(a.residual) <= MoneyAudit.tolerance(a.moved()));
    }

    /* ============================ 5. THE CITY'S OWN FARMS ============================ */

    static void theCitysFarms(Game g) {
        out.println("--- 5. the city's own Wind Farm builds in the reserve apart from the fields' ground, and gives it back whatever the reserve is by then ---");
        LandManager land = g.getLandManager();
        EconomyManager em = g.getEconomyManager();
        Sector fields = g.getSectors().byKey(FIELDS);
        BuildingsTemplate wind = g.getBuildingManager().getTemplateByName("Wind Farm");
        BuildingsTemplate field = LongPlaytest.template(g, "Mixed Farm");
        double sq = wind.getLandSqFt();
        quietly(() -> land.updateMarket(g.getPopulationManager().getPopulation()));

        // ...a field outside the zone first (REV-RG1): the zone shrunk under what the fields built in it, so it holds nothing, a field bought, the zone grown back.
        double usedIn = land.usedBy(FIELDS);
        quietly(() -> g.setZone(FIELDS, Zoning.Size.KM2, LandManager.km2(usedIn) * 0.9, DIAL));
        double heldNone = land.heldBy(FIELDS);
        boolean outside = g.buildFor(g.getSectorInvestor(FIELDS), field, 1);
        quietly(() -> g.setZone(FIELDS, Zoning.Size.KM2, RESERVE_KM2, DIAL));
        double all = g.getBuildingManager().getLandSqFtBySector(FIELDS);
        check(String.format("fixture: a field bought outside the zone (it held %s): the fields' ground %s, %s of it in the zone, the reserve standing",
                        LandManager.areaWords(heldNone), LandManager.areaWords(all), LandManager.areaWords(land.usedBy(FIELDS))),
                outside && heldNone == 0 && usedIn > 0 && land.usedBy(FIELDS) == usedIn && all > usedIn && g.getZoning().reserve() != null);

        double used = land.usedBy(FIELDS), held = land.heldBy(FIELDS), farms = land.cityFarmsInReserve();
        double cash = g.getCash(), sales = land.getLandSalesThisMonth();
        double assessed = em.assessedLandValueOf(fields), buyback = land.buybackPriceFor(all, FIELDS, LandManager.Use.BUILDING);
        check("a Wind Farm's ground is the reserve's: its key is the fields' while the reserve stands",
                FIELDS.equals(land.reserveKeyOf(wind)) && FIELDS.equals(land.groundKeyOf(wind)) && held >= sq);
        Game.BuildResult[] r = new Game.BuildResult[1];
        quietly(() -> r[0] = g.buildStack(wind, 1, true));
        check(String.format("the city builds one (%s): it takes %s of the reserve's held ground, counted as the city's farms' and not as the fields'",
                        r[0], LandManager.areaWords(sq)),
                r[0] == Game.BuildResult.SUCCESS && land.usedBy(FIELDS) == used && land.cityFarmsInReserve() - farms == sq
                        && close(held - land.heldBy(FIELDS), sq, 1e-9));
        out.printf("      the fields' land on the roll %s and %s; bought back whole %s and %s%n", Formats.INSTANCE.cash(assessed),
                Formats.INSTANCE.cash(em.assessedLandValueOf(fields)), Formats.INSTANCE.cash(buyback),
                Formats.INSTANCE.cash(land.buybackPriceFor(all, FIELDS, LandManager.Use.BUILDING)));
        check("...so the fields' land on the roll, and what the city would buy all of it back at, are as before: their ground outside the zone at the going price still",
                close(em.assessedLandValueOf(fields), assessed, 1e-12) && close(land.buybackPriceFor(all, FIELDS, LandManager.Use.BUILDING), buyback, 1e-12));
        check(String.format("...and pays for the building alone (%s, an instant build's cash), nothing for the ground, its own",
                        Formats.INSTANCE.cash(cash - g.getCash())),
                close(cash - g.getCash(), wind.getCashCost(), 1e-9) && land.getLandSalesThisMonth() == sales);
        int months = demolishOne(g, wind);
        check(String.format("demolished (%d months), its ground goes back to the reserve out of the city's farms', the fields' used ground untouched", months),
                months >= 0 && land.cityFarmsInReserve() == farms && land.usedBy(FIELDS) == used);

        // ...both orders of building and lifting (REV-RG1): the ground goes back by where it came from, not by whether a reserve stands at the end.
        quietly(() -> g.setFarmlandReserve(false));
        double before = land.usedBy(FIELDS), farmsBefore = land.cityFarmsInReserve();
        quietly(() -> g.buildStack(wind, 1, true));
        boolean apart = g.getZoning().reserve() == null && land.reserveKeyOf(wind) == null && land.usedBy(FIELDS) == before
                && land.cityFarmsInReserve() == farmsBefore;
        quietly(() -> g.setFarmlandReserve(true));
        int first = demolishOne(g, wind);
        check(String.format("built with the reserve lifted, a Wind Farm leaves the zone alone; demolished under the reserve (%d months), "
                        + "the fields' used ground is exact (%s)", first, LandManager.areaWords(land.usedBy(FIELDS))),
                apart && first >= 0 && land.usedBy(FIELDS) == before && land.cityFarmsInReserve() == farmsBefore);
        quietly(() -> g.buildStack(wind, 1, true));
        boolean in = land.cityFarmsInReserve() - farmsBefore == sq && land.usedBy(FIELDS) == before;
        quietly(() -> g.setFarmlandReserve(false));
        int second = demolishOne(g, wind);
        check(String.format("built in the reserve and demolished with it lifted (%d months): the city's farms' ground goes back, the fields' used ground exact",
                        second),
                in && second >= 0 && land.cityFarmsInReserve() == farmsBefore && land.usedBy(FIELDS) == before);
        quietly(() -> g.setFarmlandReserve(true));
    }

    /** Orders one of the city's Wind Farms demolished and presses months until its site is done and its ground given back (Game.settleConstructionControl()): the months, or -1 when refused or never done. */
    static int demolishOne(Game g, BuildingsTemplate wind) {
        if (!g.demolish(wind, 1)) return -1;
        int months = 0;
        while (onSite(g, wind)) {
            if (months++ >= 60) return -1;
            quietly(g::toggleNextMonth);
        }
        return months;
    }

    /** Whether a demolition of this kind is on site. */
    static boolean onSite(Game g, BuildingsTemplate t) {
        for (ConstructionControl.Demolition d : g.getBuildingManager().getControl().demolitions()) if (d.templateId == t.getId()) return true;
        return false;
    }

    /* ============================ 6. THE PLANNER ============================ */

    /** The free ground the crowded town keeps past what it has built, square kilometres: room for the reserve and a little for anyone. */
    static final double CROWDED_FREE_KM2 = RESERVE_KM2 + 1;

    static void thePlanner(Path root) {
        out.println("--- 6. the planner builds farms where the default built none ---");
        Game g = PowerPlanCheck.covered(root, "dear", PowerPlanCheck.DIESEL);
        LandManager land = g.getLandManager();
        BuildingsTemplate wind = PowerPlanCheck.plant(g, PowerPlanCheck.WIND), solar = PowerPlanCheck.plant(g, PowerPlanCheck.SOLAR);
        BuildAdvice.Suggestion s = PowerPlanCheck.suggest(g);
        check("fixture: PowerPlanCheck 6's dear town (diesels standing) orders wind on its own ground (" + named(s) + ")",
                s != null && s.template() == wind);

        land.setOwnedSqFt(land.getAllocatedSqFt() + LandManager.sqFt(CROWDED_FREE_KM2));
        land.getMarket().putOffers(List.of());
        long crowd = (long) (2 * LandMarket.CROWDING_MIDPOINT * land.getOwnedSqFt() / LandManager.SQ_FT_PER_KM2);
        quietly(() -> land.updateMarket(crowd));
        double windGround = share(g, wind), solarGround = share(g, solar);
        s = PowerPlanCheck.suggest(g);
        check(String.format("crowded (the premium %.0f, the land office relisted at it), a Wind Farm's ground is %.0f%% of its capital and a Solar Farm's %.0f%%",
                        land.getCrowdingPremium(), 100 * windGround, 100 * solarGround),
                land.getCrowdingPremium() > LandMarket.CROWDING_CEILING / 2 && windGround > 0.5 && solarGround > 0.5);
        check("...so the town orders no wind (" + named(s) + ")", s == null || s.template() != wind);

        quietly(() -> g.setZone(FIELDS, Zoning.Size.KM2, RESERVE_KM2, 1));
        s = PowerPlanCheck.suggest(g);
        check(String.format("the fields' zone at the going price is no reserve: it holds %s for the fields alone, and the town still orders none (%s)",
                        LandManager.areaWords(land.heldBy(FIELDS)), named(s)),
                land.heldBy(FIELDS) >= solar.getLandSqFt() && (s == null || s.template() != wind));

        quietly(() -> g.setFarmlandReserve(true));
        double farm = land.groundPriceFor(FIELDS), left = land.getAvailableSqFt();
        double windNow = share(g, wind), solarNow = share(g, solar);
        check(String.format("with the reserve, a farm's ground is at the farmland price (%.3g a square foot, the going %.3g): a Wind Farm's %.1f%% of its capital, a Solar Farm's %.1f%%",
                        farm, land.getPricePerSqFt(), 100 * windNow, 100 * solarNow),
                close(farm, land.getFarmlandPricePerSqFt(), 1e-12)
                        && close(BuildAdvice.capitalOf(g, wind, 1, left) - g.quoteBuild(wind, 1).total, wind.getLandSqFt() * farm, 1e-9)
                        && close(BuildAdvice.capitalOf(g, solar, 1, left) - g.quoteBuild(solar, 1).total, solar.getLandSqFt() * farm, 1e-9)
                        && windNow < windGround / 10 && solarNow < solarGround / 10);
        long spread0 = BuildAdvice.SPREAD_ORDERS.get();
        s = PowerPlanCheck.suggest(g);
        check("...and the town orders wind again (" + named(s) + "), the spread's order", s != null && s.template() == wind
                && BuildAdvice.SPREAD_ORDERS.get() == spread0 + 1);
        check("...its ground needing none bought: the reserve holds it", AutoBuilder.lacks(g, wind, s == null ? 1 : s.count()) <= 0);
    }

    /** One `t`'s ground as a share of its capital at the planner's (BuildAdvice.capitalOf() on the ground free for anyone). */
    static double share(Game g, BuildingsTemplate t) {
        double cap = BuildAdvice.capitalOf(g, t, 1, g.getLandManager().getAvailableSqFt());
        return cap > 0 ? (cap - g.quoteBuild(t, 1).total) / cap : Double.NaN;
    }

    static String named(BuildAdvice.Suggestion s) { return s == null ? "none" : s.count() + " " + s.template().getName(); }

    /* ============================ 7. THE SAVE ============================ */

    static void theSave(Path root, Game g) {
        out.println("--- 7. saved and reloaded, the same reserve; an older save's zone at its share ---");
        LandManager land = g.getLandManager();
        // ...saved between two presses, nothing set by hand since the last (ZoningCheck plays 24 months): a station the city built by
        // hand between presses is a month the save does not carry, and its twin's cash differs a month on with no reserve at all (RG1's notes).
        // ...with a Wind Farm of the city's in the reserve, its ground counted apart (REV-RG1), built before the press.
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("Wind Farm"), 1, true));
        quietly(g::toggleNextMonth);
        Zoning.Zone was = g.getZoning().reserve();
        check("fixture: the city has its reserve, a field, a Wind Farm of its own in it and the farmland share struck",
                was != null && land.usedBy(FIELDS) > 0 && land.cityFarmsInReserve() > 0 && Double.isFinite(g.getZoning().farmlandShare()));
        quietly(() -> g.saveGame(10, "farmlandcheck"));
        // ...the twin in a folder of its own, the slot copied to it: two cities pressed a month on in one folder autosave into one history file.
        GameFiles mine = new GameFiles(root.resolve("city"), root.resolve("city-no-legacy")), its = new GameFiles(root.resolve("twin"), root.resolve("twin-no-legacy"));
        try {
            Files.createDirectories(its.savesDirectory());
            Files.copy(mine.saveFile(10), its.saveFile(10));
            Files.copy(mine.historyFile(10), its.historyFile(10));
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        Game twin = new Game(its);
        quietly(() -> twin.loadGameSave(10));
        twin.getBusinessInvestment().holdSector(Sectors.AGRICULTURE);   // the harness's hold, not the save's
        Zoning.Zone back = twin.getZoning().reserve();
        check("loaded, the fields' zone is the reserve again: its size, dial, used ground, the city's farms' ground and the share it sells at",
                back != null && back.size() == was.size() && back.kind() == was.kind() && back.dialShare() == was.dialShare()
                        && back.usedSqFt() == was.usedSqFt() && back.cityFarmSqFt() == was.cityFarmSqFt() && back.priceShare() == was.priceShare()
                        && twin.getZoning().farmlandShare() == g.getZoning().farmlandShare());
        check("...and its ground as the live city prices it", twin.getLandManager().groundPriceFor(FIELDS) == land.groundPriceFor(FIELDS)
                && twin.getLandManager().heldBy(FIELDS) == land.heldBy(FIELDS));
        quietly(() -> { g.toggleNextMonth(); twin.toggleNextMonth(); });
        check("a month on, the two are the same month: cash, the reserve's price and ground",
                Math.abs(g.getCash() - twin.getCash()) < 1e-9 && close(twin.getLandManager().groundPriceFor(FIELDS), land.groundPriceFor(FIELDS), 1e-12)
                        && twin.getLandManager().usedBy(FIELDS) == land.usedBy(FIELDS));

        String older = "{\"zones\":[{\"sector\":\"" + FIELDS + "\",\"kind\":\"KM2\",\"size\":2.0,\"priceShare\":0.5,\"usedSqFt\":0.0}],"
                + "\"protectedShare\":0.0,\"keptShare\":0.0,\"clearedSqFt\":0.0,\"clearedM3\":0.0,\"salvageM3\":0.0}";
        Zoning.State s = new com.google.gson.Gson().fromJson(older, Zoning.State.class);
        Zoning z = new Zoning();
        z.restore(s);
        check("an older save's zoning, with no reserve key, reads the fields' zone at its share, no reserve and none of the city's farms in it",
                z.reserve() == null && z.zone(FIELDS) != null && z.priceShare(FIELDS) == 0.5 && Double.isNaN(z.farmlandShare())
                        && z.zone(FIELDS).cityFarmSqFt() == 0);
    }
}
