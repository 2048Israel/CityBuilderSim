package ham.citybuildersim;

import ham.citybuildersim.sectors.Oil;
import ham.citybuildersim.sectors.Oil.Vintage;
import ham.citybuildersim.sectors.Oil.WellKind;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.RefineryFlow.Kind;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The oil industry on one page (0.7.96, batch O12; runs/spec-oil.md 2.13):
 * the city's wells by kind with what they would lift over the next ten years
 * if nothing new were built, the refinery's units with each one's spread and
 * the gate that stops one more, every product's price and month, the
 * planners' words, and the strategic reserve with its two levers.
 *
 * WHY. Oil's Operations page drew the wells as every business is drawn
 * (SectorFlow): crude out, a ring of the rate, and a grid of its own lines.
 * Since 0.7.84 a well declines, since 0.7.91 a platform holds a plateau and
 * then falls, since 0.7.93 the city's crude is two pools - and none of that
 * answered "how long does our own crude last?". The refinery's units were
 * each a building on the Build tab with no word of why the refiners order
 * one and not another, and the city's reserve, built since 0.7.85, had no
 * lever at all (fixO8-notes.md: "the reserve's Fill and Release are O12's").
 * Jerus's oil industry screen, drawn as the research's mockup 2
 * (claude/oil-and-ports-research.md 6): four figures, the wells by type and
 * their chart, the units table, the products table. These are its figures
 * and its words, held by a harness (OilViewCheck) rather than worked out in
 * the screen: the wells to Oil's own reads, the forecast to the vintages'
 * profiles and each pool's tonnes, the units and the products to the
 * refinery's picture (RefineryView, whose month and takers they are - so
 * the two pages cannot disagree), the reserve to StrategicReserve and the
 * levers to Game.fillReserve() and releaseReserve(). The screen
 * (ui/SectorScreen, OPERATIONS · THE OIL INDUSTRY) only paints them.
 *
 * WHICH MONTH. The month just run, as the refinery's picture reads it: its
 * lift by pool (Oil.getLiftedOnGround(), getLiftedAtSea()), crude's and the
 * products' clearings, the refinery's rows. None of it is saved, so after a
 * load the month's figures read "not counted" until a month runs (counted
 * is RefineryView's: the refiners kept a month). The wells, the pools, the
 * units standing, the spreads and gates, the prices and the reserve are the
 * city's state, read at any time.
 *
 * Pure: it reads the city and changes nothing (the planner's appraisals are
 * the build card's reads).
 */
public final class OilView {

    private OilView() { }

    /* =====================================================================
       THE FIGURES
       ===================================================================== */

    /** Years the wells' chart looks ahead (mockup 2's "the next ten are pale"). */
    public static final int FORECAST_YEARS = 10;

    /** Platforms the wells card names one by one and the chart draws apart; past them the rest are one line and one series. */
    public static final int PLATFORMS_LISTED = 3;

    /** The months in a year the forecast averages. */
    static final int MONTHS_A_YEAR = YearBook.MONTHS_A_YEAR;

    /** A barrel's litres, 158.987 (RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY is 30.44 days of it): the reserves in barrels. */
    public static final double LITRES_A_BARREL = 158.987;

    /** Barrels a day of crude lifted at `tonnes` a month: its litres (Refining.CRUDE_LITRES_PER_TONNE) over a barrel a day's month. */
    public static double barrelsADay(double tonnes) {
        return tonnes * Refining.CRUDE_LITRES_PER_TONNE / RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY;
    }

    /**
     * One platform as it stands.
     *
     * @param name     "Platform A", in the order they opened
     * @param depth    its field's depth at the centre, metres (0 on no field)
     * @param km       its field's distance from the founding site, whole kilometres (Oil.lengthKm(); 0 on no field)
     * @param wells    the wells in its slots
     * @param slots    its slots
     * @param oldest   its oldest well's age in months; -1 with none
     * @param lifted   tonnes of the month's offshore lift credited to it: its wells' nameplate share; NaN not counted
     */
    public record Platform(String name, double depth, int km, int wells, int slots, int oldest, double lifted) { }

    /**
     * A series of the wells' chart: the land wells, a platform, or the
     * platforms past PLATFORMS_LISTED together.
     *
     * @param name   its name in the legend
     * @param colour its colour
     * @param now    tonnes the month lifted; NaN not counted
     * @param ahead  tonnes a month each year ahead would lift if nothing new were built: year 1 the next twelve months'
     *               mean, and so on (forecast())
     */
    public record Series(String name, String colour, double now, double[] ahead) { }

    /**
     * One row of the units table: a kind of conversion unit, or the crude units (kind null).
     *
     * @param kind       the kind; null for the crude units
     * @param standing   its buildings standing, all sizes
     * @param onSite     ...and on site
     * @param feed       litres a month its standing buildings take at nameplate (the crude units' crude, as litres)
     * @param run        litres it took this month; NaN not counted
     * @param share      the share of its nameplate it ran: a unit's of its feed, the crude units' operating rate; NaN not counted
     * @param citySpread its spread a litre of feed at the city's own prices (SpreadPlanner.cityValue()); NaN for the crude units
     * @param gate       one more weighed by the planner (RefineryView.oneMore()); null with no template
     * @param spare      litres a month of its feed spare in the flow the planner weighs it in; for the crude units the
     *                   tonnes of crude the wells could lift that no refinery takes (Refining.Outlook.spareCrude())
     * @param fuelShort  the crude units only: litres a month of petrol and diesel the city wants past what its refinery
     *                   makes (Refining.Outlook.room()); NaN for a conversion unit
     * @param months     months its site would take at today's queue (Game.onSiteMonths()); 0 with none on site
     */
    public record UnitRow(Kind kind, int standing, int onSite, double feed, double run, double share, double citySpread,
                          SpreadPlanner.Candidate gate, double spare, double fuelShort, double months) {
        public boolean crude() { return kind == null; }
    }

    /**
     * One product's month, and crude's.
     *
     * @param good     the good
     * @param price    its price here, a unit (city money, thousands)
     * @param world    the world's, halfway between what it costs to import and fetches abroad
     * @param ladder   its world price over crude's, a litre against a litre (bitumen, coke and crude a tonne against a
     *                 tonne): the research's ladder (Good's THE PRICES)
     * @param made     made here this month (the refinery's row; crude, the wells' lift); NaN not counted
     * @param used     taken here, home-made and landed; NaN not counted
     * @param imported ...of it landed; NaN not counted
     * @param exported shipped; NaN not counted
     * @param takers   who took it here, largest first, each taker's name and units - then, for a product, what went into
     *                 the refiners' tanks and what was idled (RefineryView's takers, the world apart: its exported)
     */
    public record Product(Good good, double price, double world, double ladder, double made, double used, double imported,
                          double exported, List<Taker> takers) { }

    /** A taker of a product in its row: a name, and the units it took. */
    public record Taker(String name, double units) { }

    /**
     * The city's strategic reserve (StrategicReserve), and its levers' reach.
     *
     * @param standing    Strategic Reserves standing
     * @param room        tonnes their tanks hold
     * @param held        tonnes held
     * @param book        what those cost (its book, at average cost)
     * @param fill        tonnes ordered for the next clearing
     * @param release     tonnes a month released
     * @param fillMost    the most a fill would order now: Game.reserveFillMost(), the room left and what the treasury
     *                    could pay at crude's import price
     * @param importPrice a tonne of crude bought abroad
     * @param localPrice  ...and here
     * @param bought      tonnes the month's clearing bought for it, not yet settled
     * @param sold        ...and sold from it
     * @param paid        what the last strike paid for crude bought
     * @param took        ...and took in for crude sold
     */
    public record Reserve(int standing, double room, double held, double book, double fill, double release, double fillMost,
                          double importPrice, double localPrice, double bought, double sold, double paid, double took) {
        /** Tonnes of room the tanks have left. */
        public double roomLeft() { return Math.max(0, room - held); }
        /** The most a release may be set to: what it holds, or what is set already. */
        public double releaseMost() { return Math.max(held, release); }
        /** Whether there is a reserve to show: one standing, or crude still held. */
        public boolean shown() { return standing > 0 || held > 0; }
    }

    /**
     * The page.
     *
     * @param counted      whether the month's figures are this month's (the refinery's picture's): false after a load
     * @param month        the game's month
     * @param rate         the wells' operating rate, which the forecast runs at
     * @param landWells    land wells standing
     * @param platformWells platform wells in slots
     * @param platforms    the platforms standing, in the order they opened
     * @param pipeKm       kilometres of pipe standing
     * @param pipesWhole   fields whose whole pipe stands
     * @param shuttle      what the shuttle tankers cost last month (the statement's "Shuttle tankers")
     * @param groundLeft   tonnes left in the ground pool
     * @param seaLeft      ...and in the offshore pool
     * @param groundFields oil fields the city owns on dry ground
     * @param seaFields    ...and in the sea
     * @param shallowSites sea sites the city owns on fields a platform may stand on
     * @param drilledYear  land wells standing that opened in the last twelve months
     * @param wearYear     land wells that wear out in the next twelve
     * @param landOnSite   land wells on site
     * @param jacketsOnSite platforms' jackets on site
     * @param seaWellsOnSite platform wells on site
     * @param series       the chart's series: the land wells, then the platforms
     * @param crudeImported tonnes of crude the month bought abroad (the refiners' and the reserve's); NaN not counted
     * @param crudeImportBill ...and what it cost
     * @param refinersCrude tonnes of crude the refiners bought; NaN not counted
     * @param soldAbroad   what the nine products fetched abroad, the books the month struck (Game.getTradeByGood(), the
     *                     Trade tab's; restored by a load)
     * @param boughtAbroad ...and what the city paid for those it landed
     * @param units        the units table: the crude units, then each kind standing, on site, and the rest
     * @param products     crude, then the nine products in the tank's order
     * @param reserve      the strategic reserve
     * @param drilling     the wells' investors' word for the month (Game.getLastInvestment())
     * @param atSea        why nothing at sea was ordered (Oil.atSeaWord()); null when something was, or nothing was weighed
     * @param ordering     the refiners' investors' word for the month
     * @param refinery     the refinery's picture's month, which the units and products are read off
     */
    public record View(boolean counted, int month, double rate, int landWells, int platformWells, List<Platform> platforms,
                       int pipeKm, int pipesWhole, double shuttle, double groundLeft, double seaLeft, int groundFields,
                       int seaFields, int shallowSites, int drilledYear, int wearYear, int landOnSite, int jacketsOnSite,
                       int seaWellsOnSite, List<Series> series,
                       double crudeImported, double crudeImportBill, double refinersCrude, double soldAbroad,
                       double boughtAbroad, List<UnitRow> units, List<Product> products, Reserve reserve, String drilling,
                       String atSea, String ordering, RefineryView.View refinery) {

        /** The month's lift, tonnes: the two pools'; NaN not counted. */
        public double lifted() {
            double t = 0;
            for (Series s : series) t += s.now();
            return counted ? t : Double.NaN;
        }

        /** ...the land wells' alone. */
        public double liftedOnGround() { return series.isEmpty() ? Double.NaN : series.get(0).now(); }

        /** Tonnes left in both pools. */
        public double left() { return groundLeft + seaLeft; }

        /** Each year ahead's mean month, all the series together, tonnes. */
        public double[] ahead() {
            double[] a = new double[FORECAST_YEARS];
            for (Series s : series) for (int y = 0; y < a.length; y++) a[y] += s.ahead()[y];
            return a;
        }

        /** One product's row, or null. */
        public Product product(Good g) {
            for (Product p : products) if (p.good() == g) return p;
            return null;
        }

        /** The crude units' row, or a kind's. */
        public UnitRow unit(Kind k) {
            for (UnitRow u : units) if (u.kind() == k) return u;
            return null;
        }

        /** The chart at a width and a height (THE CHART). */
        public RefineryView.Picture chart(double width, double height) { return OilView.chart(this, width, height); }
    }

    /** The oil industry in `game` (pure). */
    public static View of(Game game) {
        Oil oil = game.getSectors().oil();
        Refining r = game.getSectors().refining();
        LandManager land = game.getLandManager();
        RefineryView.View refinery = RefineryView.of(game);
        boolean counted = refinery.counted();
        int month = game.getMonth();
        double rate = oil.getOperatingRate();

        /* ----- the wells ----- */
        List<Vintage> vintages = oil.vintagesNow();
        List<Oil.Platform> standing = oil.platformsNow();
        List<LandManager.SeaField> shallow = oil.shallowFields();
        int[] slots = Oil.slotsOf(standing, shallow, oil.slotsEach());
        List<List<Vintage>> dealt = deal(vintages, standing);
        double landEach = oil.wellNameplate(), seaEach = oil.platformWellNameplate();
        double groundLeft = land.getOilLeftOnGround(), seaLeft = land.getOilLeftAtSea();
        double[][] ahead = forecast(landOf(vintages), dealt, landEach, seaEach, groundLeft, seaLeft, rate, month, FORECAST_YEARS);

        // The month's lift: the ground pool's the land wells', the offshore pool's shared by each platform's nameplate.
        double ground = counted ? Math.max(0, oil.getLiftedOnGround()) : Double.NaN;
        double sea = counted ? Math.max(0, oil.getLiftedAtSea()) : Double.NaN;
        double[] asks = new double[dealt.size()];
        double askAll = 0;
        for (int i = 0; i < asks.length; i++) {
            asks[i] = ask(dealt.get(i), seaEach, WellKind.PLATFORM, month);
            askAll += asks[i];
        }
        long[] site = { game.getCityLand().siteX(), game.getCityLand().siteY() };
        List<Platform> platforms = new ArrayList<>();
        for (int i = 0; i < standing.size(); i++) {
            Oil.Platform p = standing.get(i);
            LandManager.SeaField f = fieldOf(shallow, p.cell(), p.index());
            int oldest = -1;
            for (Vintage v : dealt.get(i)) oldest = Math.max(oldest, month - v.month());
            double lifted = !counted ? Double.NaN : askAll > 0 ? sea * asks[i] / askAll : 0;
            platforms.add(new Platform(platformName(i), f == null ? 0 : f.depth(),
                    f == null ? 0 : Oil.lengthKm(f.field(), site[0], site[1]), p.wells(), slots[i], oldest, lifted));
        }
        List<Series> series = new ArrayList<>();
        series.add(new Series("Land wells", LAND, ground, ahead[0]));
        double restNow = 0;
        double[] rest = new double[FORECAST_YEARS];
        for (int i = 0; i < platforms.size(); i++) {
            if (i < PLATFORMS_LISTED) {
                series.add(new Series(platforms.get(i).name(), PLATFORM_COLOURS[i], platforms.get(i).lifted(), ahead[i + 1]));
            } else {
                restNow += platforms.get(i).lifted();
                for (int y = 0; y < rest.length; y++) rest[y] += ahead[i + 1][y];
            }
        }
        if (platforms.size() > PLATFORMS_LISTED) series.add(new Series("Other platforms", OTHER_PLATFORMS, restNow, rest));

        int drilled = 0, wear = 0;
        for (Vintage v : vintages) {
            if (v.kind() != WellKind.LAND) continue;
            int age = month - v.month();
            if (age < MONTHS_A_YEAR) drilled += v.count();
            if (age < Oil.lifeMonths(WellKind.LAND) && age + MONTHS_A_YEAR >= Oil.lifeMonths(WellKind.LAND)) wear += v.count();
        }
        int[] fields = fieldsByPool(game);
        int shallowSites = 0;
        for (LandManager.SeaField f : shallow) shallowSites += f.sites();
        int whole = oil.pipeKmStanding() <= 0 ? 0
                : Oil.pipedFields(oil.pipelinesNow(), oil.pipeKmStanding(), shallow, site[0], site[1]).size();
        Double shuttle = oil.statement() == null ? null : oil.statement().otherInputs.get(Oil.SHUTTLE_TANKERS);

        /* ----- crude's month, and the products' across the edge ----- */
        double crudeIn = 0, crudeBill = 0, refiners = 0, crudeOut = 0, toRefiners = 0, toReserve = 0;
        GoodsMarket crude = game.getMarkets().get(Good.CRUDE);
        if (crude != null) {
            for (Trade t : crude.getTrades()) {
                if (t.isImport()) {
                    crudeIn += t.units();
                    crudeBill += t.value();
                }
                if (t.isExport()) crudeOut += t.units();
                if (Sectors.REFINING.equals(t.buyer())) {
                    refiners += t.units();
                    toRefiners += t.units();
                }
                if (Trade.CITY.equals(t.buyer())) toReserve += t.units();
            }
        }
        // ...the products' money across the edge, the Trade tab's (Game.getTradeByGood(): the books the month struck).
        double sold = 0, bought = 0;
        Map<Good, Sectors.GoodTrade> edge = game.getTradeByGood().goods();
        for (Good g : RefineryView.TANK_ORDER) {
            Sectors.GoodTrade t = edge.get(g);
            if (t == null) continue;
            sold += t.sold();
            bought += t.bought();
        }

        /* ----- the units ----- */
        List<UnitRow> units = units(game, r, refinery);

        /* ----- the products ----- */
        List<Product> products = new ArrayList<>();
        List<Taker> crudeTakers = new ArrayList<>();
        if (toRefiners > 0) crudeTakers.add(new Taker("the refiners", toRefiners));
        if (toReserve > 0) crudeTakers.add(new Taker("the city's reserve", toReserve));
        crudeTakers.sort((a, b) -> Double.compare(b.units(), a.units()));
        products.add(new Product(Good.CRUDE, priceOf(game, Good.CRUDE), worldOf(game, Good.CRUDE), ladder(Good.CRUDE),
                counted ? ground + sea : Double.NaN, counted ? toRefiners + toReserve : Double.NaN,
                counted ? crudeIn : Double.NaN, counted ? crudeOut : Double.NaN,
                counted ? Collections.unmodifiableList(crudeTakers) : List.of()));
        for (Good g : RefineryView.TANK_ORDER) {
            RefineryView.Product p = refinery.product(g);
            List<Taker> takers = new ArrayList<>(), kept = new ArrayList<>();
            for (RefineryView.Buyer b : refinery.buyers()) {
                if (b.kind() == RefineryView.BuyerKind.ABROAD || !(b.of(g) > 0)) continue;
                boolean home = b.kind() != RefineryView.BuyerKind.TANKS && b.kind() != RefineryView.BuyerKind.IDLED;
                (home ? takers : kept).add(new Taker(RefineryView.buyerName(b), b.of(g)));
            }
            takers.sort((a, b) -> Double.compare(b.units(), a.units()));
            // ...then what went into the refiners' tanks, and what was idled (RefineryView's last takers but the world).
            takers.addAll(kept);
            double made = !counted ? Double.NaN : p == null ? 0 : p.made();
            double used = !counted ? Double.NaN : p == null ? 0 : p.used();
            double imported = !counted ? Double.NaN : p == null ? 0 : p.imported();
            double exported = !counted ? Double.NaN : p == null ? 0 : p.exported();
            products.add(new Product(g, priceOf(game, g), worldOf(game, g), ladder(g), made, used, imported, exported,
                    Collections.unmodifiableList(takers)));
        }

        /* ----- the reserve ----- */
        StrategicReserve res = game.getReserve();
        int reserves = 0;
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (StrategicReserve.isReserve(t)) reserves += game.getBuildingManager().getQuantity(t.getId());
        }
        Reserve reserve = new Reserve(reserves, StrategicReserve.room(game.getBuildingManager()), res.getTonnes(), res.getCost(),
                res.getFill(), res.getRelease(), game.reserveFillMost(), crude == null ? Double.NaN : crude.importPrice(),
                crude == null ? Double.NaN : crude.getLocalPrice(), res.getBoughtHomeTonnes() + res.getBoughtAbroadTonnes(),
                res.getSoldHomeTonnes() + res.getSoldAbroadTonnes(), res.getSettledBought(), res.getSettledSold());

        return new View(counted, month, rate, oil.landWellsStanding(), oil.platformWellsInSlots(),
                Collections.unmodifiableList(platforms), oil.pipeKmStanding(), whole, shuttle == null ? 0 : shuttle, groundLeft,
                seaLeft, fields[0], fields[1], shallowSites, drilled, wear,
                Math.max(0, game.landWellsCommitted() - oil.landWellsStanding()), oil.jacketsOnSite(),
                Math.max(0, oil.platformWellsCommitted() - oil.platformWellsStanding()), Collections.unmodifiableList(series),
                counted ? crudeIn : Double.NaN, counted ? crudeBill : Double.NaN, counted ? refiners : Double.NaN,
                sold, bought, Collections.unmodifiableList(units),
                Collections.unmodifiableList(products), reserve, game.getLastInvestment(Sectors.OIL), oil.atSeaWord(),
                game.getLastInvestment(Sectors.REFINING), refinery);
    }

    /** A platform's name by its place: "Platform A" to "Platform Z", then "Platform 27" on. */
    static String platformName(int i) {
        return "Platform " + (i < 26 ? String.valueOf((char) ('A' + i)) : String.valueOf(i + 1));
    }

    private static LandManager.SeaField fieldOf(List<LandManager.SeaField> fields, int cell, int index) {
        for (LandManager.SeaField f : fields) if (f.field().cell() == cell && f.field().index() == index) return f;
        return null;
    }

    /** The oil fields the city owns, {on dry ground, in the sea}: each field once, by where its centre lies (World.depthAt()). */
    static int[] fieldsByPool(Game game) {
        CityLand l = game.getCityLand();
        World world = World.of(l.seed());
        java.util.Set<Long> dry = new java.util.HashSet<>(), wet = new java.util.HashSet<>();
        for (List<CityLand.Held> holding : l.heldFields(Resource.OIL)) {
            for (CityLand.Held h : holding) {
                long id = ((long) h.field().cell() << 32) | (h.field().index() & 0xffffffffL);
                (world.depthAt(h.field().x(), h.field().y()) > 0 ? wet : dry).add(id);
            }
        }
        return new int[] { dry.size(), wet.size() };
    }

    /** A product's price here, a unit; NaN with no market. */
    static double priceOf(Game game, Good g) {
        GoodsMarket m = game.getMarkets().get(g);
        return m == null ? Double.NaN : m.getLocalPrice();
    }

    /** ...the world's: halfway between its import and its export price, in city money. */
    static double worldOf(Game game, Good g) {
        GoodsMarket m = game.getMarkets().get(g);
        return m == null ? Double.NaN : (m.importPrice() + m.exportPrice()) / 2;
    }

    /** A good's world price over crude's (pure): a litre against a litre of crude (Refining.CRUDE_LITRES_PER_TONNE), a tonne good against a tonne. */
    public static double ladder(Good g) {
        double crude = (Good.CRUDE.worldImportPrice() + Good.CRUDE.worldExportPrice()) / 2;
        double mid = (g.worldImportPrice() + g.worldExportPrice()) / 2;
        return Double.isFinite(g.litresPerTonne()) ? mid / (crude / Refining.CRUDE_LITRES_PER_TONNE) : mid / crude;
    }

    /* =====================================================================
       THE WELLS AHEAD: what the wells standing would lift if nothing new
       were built

       Each vintage of wells (Oil.vintagesNow()) lifts its kind's nameplate
       times its profile at its age (Oil.profile()), at the wells' operating
       rate, until it wears out (Oil.lifeMonths(): retired at the top of the
       month it reaches it), each pool's wells no more than the pool has left
       - the land wells the ground pool, the platform wells the offshore pool
       (LandManager's THE TWO OIL POOLS), the pool shared by the platforms'
       asks when it runs short. The platform wells are dealt to the platforms
       as Oil.platformView() deals them, oldest vintage to the oldest
       platform, and stay with it (★ O12-2). A year's figure is its twelve
       months' mean, from the month after this one. Nothing is drilled, no
       platform is built, the rate does not move: "if nothing new is built"
       (mockup 2).
       ===================================================================== */

    /** The land vintages, oldest first. */
    static List<Vintage> landOf(List<Vintage> vintages) {
        List<Vintage> out = new ArrayList<>();
        for (Vintage v : vintages) if (v.kind() == WellKind.LAND) out.add(v);
        return out;
    }

    /** The platform vintages dealt to the platforms, oldest to the oldest, each platform its wells in slots (pure). */
    public static List<List<Vintage>> deal(List<Vintage> vintages, List<Oil.Platform> platforms) {
        List<List<Vintage>> out = new ArrayList<>();
        List<int[]> left = new ArrayList<>();
        List<Vintage> sea = new ArrayList<>();
        for (Vintage v : vintages) {
            if (v.kind() != WellKind.PLATFORM) continue;
            sea.add(v);
            left.add(new int[] { v.count() });
        }
        int at = 0;
        for (Oil.Platform p : platforms) {
            List<Vintage> mine = new ArrayList<>();
            int want = Math.max(0, p.wells());
            while (want > 0 && at < sea.size()) {
                int take = Math.min(want, left.get(at)[0]);
                if (take > 0) mine.add(new Vintage(sea.get(at).month(), take, WellKind.PLATFORM));
                want -= take;
                left.get(at)[0] -= take;
                if (left.get(at)[0] == 0) at++;
            }
            out.add(mine);
        }
        return out;
    }

    /** What `vintages` of `kind` ask in `month` at nameplate (pure): each one's wells x `each` x its profile at its age, nothing once worn out. */
    public static double ask(List<Vintage> vintages, double each, WellKind kind, int month) {
        double a = 0;
        for (Vintage v : vintages) {
            int age = month - v.month();
            if (age >= Oil.lifeMonths(kind)) continue;
            a += v.count() * each * Oil.profile(kind, age);
        }
        return a;
    }

    /**
     * The years ahead (pure): {the land wells', then each platform's} mean
     * tonnes a month in each of `years` years, the first from `month` + 1,
     * at `rate`, each pool's lift no more than it has left.
     */
    public static double[][] forecast(List<Vintage> land, List<List<Vintage>> platforms, double landEach, double seaEach,
                                      double groundLeft, double seaLeft, double rate, int month, int years) {
        double[][] out = new double[1 + platforms.size()][years];
        double ground = Math.max(0, groundLeft), sea = Math.max(0, seaLeft);
        double[] asks = new double[platforms.size()];
        for (int j = 0; j < years * MONTHS_A_YEAR; j++) {
            int t = month + 1 + j, y = j / MONTHS_A_YEAR;
            double l = Math.min(ask(land, landEach, WellKind.LAND, t) * rate, ground);
            ground -= l;
            out[0][y] += l / MONTHS_A_YEAR;
            double all = 0;
            for (int i = 0; i < asks.length; i++) {
                asks[i] = ask(platforms.get(i), seaEach, WellKind.PLATFORM, t) * rate;
                all += asks[i];
            }
            double lifted = Math.min(all, sea);
            sea -= lifted;
            for (int i = 0; i < asks.length; i++) out[i + 1][y] += (all > 0 ? asks[i] * lifted / all : 0) / MONTHS_A_YEAR;
        }
        return out;
    }

    /* =====================================================================
       THE UNITS: the crude units, then every kind of conversion unit -
       standing, then on site, then the rest - each with what it turns into
       what, its nameplate, how much of it ran, its spread at the city's own
       prices (what the planner weighs), and one more of it weighed by the
       planner: the gate that stops it, or what it would earn.
       ===================================================================== */

    static List<UnitRow> units(Game game, Refining r, RefineryView.View refinery) {
        BuildingManager b = game.getBuildingManager();
        Map<Kind, List<BuildingsTemplate>> templates = new EnumMap<>(Kind.class);
        List<BuildingsTemplate> crudeUnits = new ArrayList<>();
        int crudeOnSite = 0;
        BuildingsTemplate crudeSite = null;
        Map<Kind, BuildingsTemplate> sites = new EnumMap<>(Kind.class);
        for (BuildingsTemplate t : b.getTemplatesBySector(Sectors.REFINING)) {
            BuildingsStacks stack = b.getStack(t);
            int site = stack == null ? 0 : stack.getUnderConstruction();
            if (Refining.isCrudeUnit(t)) {
                crudeUnits.add(t);
                crudeOnSite += site;
                if (site > 0 && crudeSite == null) crudeSite = t;
            } else if (Refining.isConversionUnit(t)) {
                templates.computeIfAbsent(t.refineryUnit(), x -> new ArrayList<>()).add(t);
                if (site > 0) sites.putIfAbsent(t.refineryUnit(), t);
            }
        }
        BusinessInvestment plans = game.getBusinessInvestment();
        Refining.Outlook o = plans == null ? null : r.outlook(plans);
        RefineryFlow.Flow weighed = o == null ? null : RefineryFlow.solve(o.feed(), o.crude(), o.mix(), o.values());
        double[] values = r.cityValues();
        double[] feed = r.unitFeed(false);

        List<UnitRow> rows = new ArrayList<>();
        // The crude units: their crude as litres, the month's rate, one more of the size that passes and earns most.
        int crudeStanding = refinery.crudeUnits();
        double crudeFeed = refinery.flow().crude();
        double crudeRun = refinery.counted() ? crudeFeed * refinery.rate() : Double.NaN;
        rows.add(new UnitRow(null, crudeStanding, crudeOnSite, crudeFeed, crudeRun,
                refinery.counted() && crudeStanding > 0 ? refinery.rate() : Double.NaN, Double.NaN,
                oneMoreCrude(r, plans, crudeUnits), o == null ? Double.NaN : Math.max(0, o.spareCrude()),
                o == null ? Double.NaN : Math.max(0, o.room()), crudeSite == null ? 0 : game.onSiteMonths(crudeSite)));

        List<UnitRow> standing = new ArrayList<>(), building = new ArrayList<>(), rest = new ArrayList<>();
        for (Kind k : Kind.values()) {
            RefineryView.Unit u = refinery.unit(k);
            List<BuildingsTemplate> sizes = templates.get(k);
            SpreadPlanner.Candidate gate = u != null ? u.gate() : RefineryView.oneMore(r, game, sizes);
            double spare = weighed == null ? Double.NaN : Math.max(0, Refining.spareFeed(weighed, k));
            int on = u == null ? 0 : u.standing(), site = u == null ? 0 : u.onSite();
            double run = u == null ? (refinery.counted() ? 0 : Double.NaN) : u.run();
            double share = u == null ? Double.NaN : u.share();
            double months = sites.containsKey(k) ? game.onSiteMonths(sites.get(k)) : 0;
            UnitRow row = new UnitRow(k, on, site, feed[k.ordinal()], run, share, RefineryFlow.spread(k, values), gate, spare,
                    Double.NaN, months);
            (on > 0 ? standing : site > 0 ? building : rest).add(row);
        }
        rows.addAll(standing);
        rows.addAll(building);
        rows.addAll(rest);
        return rows;
    }

    /** One more crude unit, weighed by the planner (Refining.appraise(), the build card's read): of its sizes, the one that passes and earns most on its cost, or the smallest's refusal. */
    static SpreadPlanner.Candidate oneMoreCrude(Refining r, BusinessInvestment plans, List<BuildingsTemplate> sizes) {
        if (plans == null || sizes.isEmpty()) return null;
        List<BuildingsTemplate> bySize = new ArrayList<>(sizes);
        bySize.sort(java.util.Comparator.comparingDouble(t -> t.uses(Good.CRUDE)));
        SpreadPlanner.Candidate first = null, best = null;
        for (BuildingsTemplate t : bySize) {
            SpreadPlanner.Candidate c = r.appraise(t, plans);
            if (first == null) first = c;
            if (c.passes() && c.cost() > 0 && c.earns() > 0 && (best == null || c.score() > best.score())) best = c;
        }
        return best != null ? best : first;
    }

    /* =====================================================================
       THE LAYOUT (mockup 2's, at 1,389 x 868): the page's 1,234 px, the
       figures four tiles across it, the wells' box 548 px beside the units',
       the products and the reserve each a box across it. The screen lays the
       boxes out at these; the words probe measures each word against them.
       ===================================================================== */

    /** The page's width at the 1,389 x 868 window (the refinery picture's card's 1,234). */
    public static final double PAGE_AT_1389 = 1234;

    /** A box's padding across and down, and its border (mockup 2's .box: 10 px 12 px, 1 px). */
    public static final double BOX_PAD_X = 12, BOX_PAD_Y = 10, BOX_EDGE = 1;

    /** The gap between the boxes and between the figures' tiles (mockup 2's 10 and 8). */
    public static final double BOX_GAP = 10, TILE_GAP = 8;

    /** The wells' box's width (mockup 2's 548); the units' box takes the rest of the row. */
    public static final double WELLS_W = 548;

    /** The chart's width: the wells' box inside its padding and border (mockup 2's 522). */
    public static final double CHART_W = WELLS_W - 2 * BOX_PAD_X - 2 * BOX_EDGE;

    /** A figure tile's padding across (mockup 2's .fig: 12 px), and a wells card's (.wc: 10 px). */
    public static final double TILE_PAD_X = 12, CARD_PAD_X = 10, CARD_GAP = 8;

    /** The sizes the page sets its words in (mockup 2's): a figure, a tile's line, a card's line, a table's cells and its notes. */
    public static final double FIGURE_SIZE = 21, TILE_WORDS = 11, CARD_WORDS = 11, CARD_HEAD = 12, CELL = 11.5, CELL_NOTE = 10.5,
            HEAD_WORDS = 10, PILL_WORDS = 10;

    /** A series' or a product's swatch, and the gap after it (mockup 2's .dot: 10 px; .plat: 6 px). */
    public static final double DOT = 10, DOT_GAP = 6;

    /** A pill's padding across and the gap after it (mockup 2's .pill: 6 px, and the cell's space). */
    public static final double PILL_PAD_X = 6, PILL_GAP = 6;

    /** A table cell's padding across (mockup 2's td: 6 px). */
    public static final double CELL_PAD_X = 6;

    /* =====================================================================
       THE CHART (mockup 2's LOCAL CRUDE · B/D): a bar a year, the month just
       run solid and the ten years ahead pale, each series stacked in its
       colour, barrels a day on a nice scale (ChartModel.niceScale()), a
       dashed line after now with its figure. Laid out here in the refinery
       picture's shapes (RefineryView.Picture), so the screen's paint() and a
       check draw the same chart.
       ===================================================================== */

    /** The chart's height on the page (mockup 2's 146 px). */
    public static final double CHART_HEIGHT = 146;

    /** Room at the left for the scale's figures, under the bars for the years, over them, and at the right (mockup 2's 40, 20, 10, 8). */
    static final double AXIS_W = 40, YEARS_H = 20, CHART_TOP = 10, CHART_RIGHT = 8;

    /** The gap between bars, and a bar's opacity now and ahead (mockup 2's 3 px, .95 and .32). */
    static final double BAR_GAP = 3, NOW_OPACITY = .95, AHEAD_OPACITY = .32;

    /** Gridlines the scale aims at. */
    static final int GRIDLINES = 5;

    /** Every how many bars a year is written under the axis (mockup 2's five). */
    static final int YEAR_EVERY = 5;

    /** The faces' sizes: the scale's and the years' figures, the now line's words (mockup 2's 9.5). */
    static final double CHART_WORDS = 9.5;

    /** The land wells' colour (mockup 2's, the ore's), and the platforms' (mockup 2's two blues; a lighter and a darker step of the same hue for a third and the rest, ★ O12-6). */
    public static final String LAND = "#c9b68f";
    public static final String[] PLATFORM_COLOURS = { "#4fa3c7", "#2c6f8f", "#86c6e2" };
    public static final String OTHER_PLATFORMS = "#1d4b62";

    /** A gridline, and the now line (mockup 2's). */
    public static final String GRID = "#1d2b3c", ACCENT = "#5aa9ff";

    /** The chart at `width` x `height` (pure; mockup 2's chart of the wells, laid out as it lays it). */
    static RefineryView.Picture chart(View v, double width, double height) {
        int n = 1 + FORECAST_YEARS;
        double plot = width - AXIS_W - CHART_RIGHT, bw = plot / n, bottom = height - YEARS_H;
        double[][] bars = new double[n][v.series().size()];
        double most = 0;
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int s = 0; s < v.series().size(); s++) {
                Series x = v.series().get(s);
                double t = i == 0 ? x.now() : x.ahead()[i - 1];
                bars[i][s] = Double.isFinite(t) ? Math.max(0, barrelsADay(t)) : 0;
                sum += bars[i][s];
            }
            most = Math.max(most, sum);
        }
        ChartModel.Scale scale = most > 0 ? ChartModel.niceScale(0, most, GRIDLINES, true) : new ChartModel.Scale(0, 1, 1);
        List<RefineryView.Box> boxes = new ArrayList<>();
        List<RefineryView.Line> lines = new ArrayList<>();
        List<RefineryView.Words> words = new ArrayList<>();
        List<RefineryView.Place> places = new ArrayList<>();
        double span = bottom - CHART_TOP;
        for (double tick : scale.ticks()) {
            if (!(most > 0) && tick > 0) continue;
            double y = scale.y(tick, bottom, span);
            lines.add(new RefineryView.Line(AXIS_W, y, width, y, GRID));
            words.add(new RefineryView.Words(axisWords(tick), AXIS_W - 6, y + 3.5, RefineryView.Face.MONO, CHART_WORDS,
                    RefineryView.TEXT_3, RefineryView.Align.END, AXIS_W - 6, false));
        }
        for (int i = 0; i < n; i++) {
            double x = AXIS_W + 4 + i * bw, acc = 0;
            for (int s = 0; s < v.series().size(); s++) {
                double b = bars[i][s];
                if (!(b > 0)) continue;
                double y0 = scale.y(acc + b, bottom, span), y1 = scale.y(acc, bottom, span);
                Series x0 = v.series().get(s);
                boxes.add(new RefineryView.Box(x, y0, bw - BAR_GAP, y1 - y0, x0.colour(), i == 0 ? NOW_OPACITY : AHEAD_OPACITY,
                        null, false, false, 0, barTip(v, x0, i, b)));
                acc += b;
            }
            places.add(new RefineryView.Place("bar:" + i, x, scale.y(acc, bottom, span), bw - BAR_GAP,
                    bottom - scale.y(acc, bottom, span)));
            if (i % YEAR_EVERY == 0) {
                words.add(new RefineryView.Words(String.valueOf(CityCalendar.yearOf(v.month() + i * MONTHS_A_YEAR)),
                        x + (bw - BAR_GAP) / 2, height - 5, RefineryView.Face.MONO, CHART_WORDS, RefineryView.TEXT_3,
                        RefineryView.Align.MIDDLE, bw * YEAR_EVERY, false));
            }
        }
        double nowX = AXIS_W + 4 + bw - 1;
        lines.add(new RefineryView.Line(nowX, CHART_TOP - 4, nowX, bottom, ACCENT));
        double room = width - nowX - 3;
        words.add(new RefineryView.Words(nowWords(v), nowX + 3, CHART_TOP + 5, RefineryView.Face.SANS, CHART_WORDS, ACCENT,
                RefineryView.Align.START, room, false));
        words.add(new RefineryView.Words(AHEAD_WORDS, nowX + 3, CHART_TOP + 17, RefineryView.Face.SANS, CHART_WORDS,
                RefineryView.TEXT_3, RefineryView.Align.START, room, false));
        if (!(most > 0)) {
            words.add(new RefineryView.Words(NOTHING_LIFTED, AXIS_W + 4 + plot / 2, CHART_TOP + span / 2, RefineryView.Face.SANS,
                    CHART_WORDS + 1, RefineryView.TEXT_3, RefineryView.Align.MIDDLE, plot, false));
        }
        return new RefineryView.Picture(width, height, span / (scale.high() - scale.low()), List.of(),
                Collections.unmodifiableList(boxes), List.of(), Collections.unmodifiableList(lines), List.of(),
                Collections.unmodifiableList(words), Collections.unmodifiableList(places));
    }

    /** What the chart says with nothing to draw. */
    public static final String NOTHING_LIFTED = "no well stands, and none would lift";

    /** The scale's figures: "10k", "2.5k", "500", "2.5", "5M", "1.5B". */
    static String axisWords(double barrels) {
        if (barrels == 0) return "0";
        for (double[] u : new double[][] { { 1e9, 'B' }, { 1e6, 'M' } }) {
            if (Math.abs(barrels) < u[0]) continue;
            double x = barrels / u[0];
            return (x == Math.rint(x) ? String.format("%,.0f", x) : String.format("%,.1f", x)) + (char) u[1];
        }
        if (Math.abs(barrels) < 10 && barrels != Math.rint(barrels)) return String.format("%.1f", barrels);
        if (Math.abs(barrels) >= 1000) {
            double k = barrels / 1000;
            return (k == Math.rint(k) ? String.format("%,.0f", k) : String.format("%,.1f", k)) + "k";
        }
        return String.format("%,.0f", barrels);
    }

    /** The now line's words: "now · 45,300 b/d", or that the month is not counted. */
    static String nowWords(View v) {
        return v.counted() ? "now · " + barrelsWords(v.lifted()) : "now · not counted until a month runs";
    }

    /** ...and under them. */
    public static final String AHEAD_WORDS = "if nothing new is built ›";

    /** Under the pointer, a bar's part: "Platform A, 2031: 1,200 b/d (the next twelve months' mean)". */
    static String barTip(View v, Series s, int i, double barrels) {
        String when = i == 0 ? "this month" : CityCalendar.yearOf(v.month() + i * MONTHS_A_YEAR) + ", ahead";
        return s.name() + ", " + when + ": " + barrelsWords(tonnesOf(barrels)) + (i == 0 ? "" : " if nothing new is built");
    }

    private static double tonnesOf(double barrels) {
        return barrels * RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY / Refining.CRUDE_LITRES_PER_TONNE;
    }

    /* =====================================================================
       THE WORDS
       ===================================================================== */

    /** Crude a month as barrels a day, to three figures: "45,300 b/d"; "—" not counted. */
    public static String barrelsWords(double tonnes) {
        return Double.isFinite(tonnes) ? RefineryView.barrels(tonnes * Refining.CRUDE_LITRES_PER_TONNE) : "—";
    }

    /** Tonnes in three or four figures: "26.2 Mt", "1.42 Mt", "830k t", "640 t". */
    public static String megaTonnes(double t) {
        if (!Double.isFinite(t)) return "—";
        double a = Math.abs(t);
        if (a >= 1e9) return String.format("%,.0f Mt", t / 1e6);
        if (a >= 1e7) return String.format("%.1f Mt", t / 1e6);
        if (a >= 1e6) return String.format("%.2f Mt", t / 1e6);
        return RefineryView.compact(t) + " t";
    }

    /** A share in whole per cent: "43%". */
    static String percent(double share) {
        return Double.isFinite(share) ? Math.round(share * 100) + "%" : "—";
    }

    /** A sentence's money (Formats.amount()): "$78.8M"; what rounds to nothing, "$0". */
    static String money(double thousands) {
        String m = Formats.INSTANCE.amount(thousands);
        return "$0.00".equals(m) || "-$0.00".equals(m) ? "$0" : m;
    }

    /** ...signed: "+$11.5M", "−$2.0M". */
    static String signedMoney(double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        String m = money(Math.abs(thousands));
        return ("$0".equals(m) ? "" : thousands < 0 ? "−" : "+") + m;
    }

    /** What the page says while the month is not counted. */
    public static final String NOT_COUNTED = "not counted until a month runs";

    /* ----- the four figures (mockup 2's) ----- */

    /**
     * One of the four figures over the page.
     *
     * @param label  its label, in capitals
     * @param value  its figure
     * @param line   the line under it
     * @param colour the figure's colour
     * @param tip    what it says under the pointer
     */
    public record Figure(String label, String value, String line, String colour, String tip) { }

    /** The colour of a figure that is the answer, of good news and bad, and of a warning. */
    public static final String TEXT = RefineryView.TEXT, GOOD = "#3fb950", BAD = "#f85149", WARN = RefineryView.HOT;

    /** LIFTED HERE, RESERVES LEFT, CRUDE IMPORTED, PRODUCTS ACROSS THE EDGE. */
    public static List<Figure> figures(View v) {
        List<Figure> f = new ArrayList<>();
        int wells = v.landWells() + v.platformWells();
        String count = wells == 0 ? "no well stands" : countWords(v.landWells(), "well") + (v.platforms().isEmpty() ? ""
                : ", " + countWords(v.platforms().size(), "platform"));
        f.add(new Figure("LIFTED HERE", v.counted() ? barrelsWords(v.lifted()) : "—",
                v.counted() ? RefineryView.litres(v.lifted() * Refining.CRUDE_LITRES_PER_TONNE) + " · "
                        + RefineryView.compact(v.lifted()) + " t a month · " + count : NOT_COUNTED + " · " + count,
                TEXT, "The crude the city's wells lifted this month: the land wells from the ground pool, the platforms' from"
                        + " the offshore pool. A barrel a day is " + String.format("%,.1f", RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY)
                        + " litres a month; a tonne of crude " + String.format("%,.0f", Refining.CRUDE_LITRES_PER_TONNE) + " litres."));
        double years = v.counted() && v.lifted() > 0 ? v.left() / (v.lifted() * MONTHS_A_YEAR) : Double.NaN;
        boolean soon = Double.isFinite(years) && years < FORECAST_YEARS;
        f.add(new Figure("RESERVES LEFT", megaTonnes(v.left()),
                String.format("%,.0fM barrels · ", v.left() * Refining.CRUDE_LITRES_PER_TONNE / LITRES_A_BARREL / 1e6)
                        + (Double.isFinite(years) ? String.format("%.1f years at today's rate", years)
                        : v.counted() ? "no well is lifting" : NOT_COUNTED),
                TEXT, "The crude still under the city's own ground and sea: the ground pool, which the land wells lift ("
                        + megaTonnes(v.groundLeft()) + "), and the offshore pool, which the platforms lift ("
                        + megaTonnes(v.seaLeft()) + "). The years are what is left over this month's lift."
                        + (soon ? " At this rate it runs out within the chart's ten years." : "")));
        double share = v.refinersCrude() > 0 ? v.crudeImported() / v.refinersCrude() : Double.NaN;
        f.add(new Figure("CRUDE IMPORTED", v.counted() ? barrelsWords(v.crudeImported()) : "—",
                !v.counted() ? NOT_COUNTED : !(v.crudeImported() > 0) ? "none this month"
                        : (Double.isFinite(share) ? percent(Math.min(1, share)) + " of the refiners' crude · " : "")
                        + money(v.crudeImportBill()) + " a month",
                TEXT, "Crude bought abroad this month, by the refiners and the city's reserve, at crude's import price."));
        double net = v.soldAbroad() - v.boughtAbroad();
        String edge = signedMoney(net);
        f.add(new Figure("PRODUCTS ACROSS THE EDGE", edge,
                money(v.soldAbroad()) + " sold abroad · " + money(v.boughtAbroad()) + " bought",
                edge.startsWith("+") ? GOOD : edge.startsWith("−") ? BAD : TEXT,
                "What the nine products the refinery makes fetched abroad, less what the city paid for those it landed -"
                        + " petrol and diesel for the drivers and the vans, and the rest: the month the books closed on, as"
                        + " the Trade tab shows it."));
        return f;
    }

    /** "160 wells", "1 well". */
    static String countWords(int n, String noun) {
        return String.format("%,d %s%s", n, noun, n == 1 ? "" : "s");
    }

    /* ----- the wells by type (mockup 2's two cards) ----- */

    /** A line of a wells card: its words, its figure, the figure's colour. */
    public record Fact(String label, String value, String colour) { }

    /** A platform's line on its card: its colour, its name, its words (depth, distance, phase), its figure and its tip. */
    public record PlatformLine(String colour, String name, String words, String figure, String tip) { }

    /** The figure's colour on a card: the secondary grey (mockup 2's .kv b). */
    public static final String FACT = RefineryView.TEXT_2;

    /** The land wells' card: lifting, a well's average, the decline, the ground pool, the wells drilled in the last year and wearing out in the next. */
    public static List<Fact> landFacts(View v) {
        List<Fact> f = new ArrayList<>();
        double lifted = v.liftedOnGround();
        f.add(new Fact("lifting", v.counted() ? barrelsWords(lifted) : "—", FACT));
        f.add(new Fact("a well, on average", v.counted() && v.landWells() > 0 ? barrelsWords(lifted / v.landWells()) : "—", FACT));
        f.add(new Fact("decline, each well", "−" + Math.round((1 - Oil.LAND_KEEPS_A_YEAR) * 100) + "% a year", WARN));
        f.add(new Fact("reserves left", megaTonnes(v.groundLeft()) + " · " + countWords(v.groundFields(), "field"), FACT));
        f.add(new Fact("drilled in the last year", "+" + v.drilledYear(), FACT));
        f.add(new Fact("wearing out in the next", "−" + v.wearYear(), v.wearYear() > 0 ? WARN : FACT));
        if (v.landOnSite() > 0) f.add(new Fact("on site", countWords(v.landOnSite(), "well"), ACCENT));
        return f;
    }

    /** The platforms' lines: one each to PLATFORMS_LISTED, then the rest as one. */
    public static List<PlatformLine> platformLines(View v) {
        List<PlatformLine> out = new ArrayList<>();
        double restLift = 0;
        int restWells = 0, rest = 0;
        for (int i = 0; i < v.platforms().size(); i++) {
            Platform p = v.platforms().get(i);
            if (i >= PLATFORMS_LISTED) {
                rest++;
                restLift += Double.isFinite(p.lifted()) ? p.lifted() : 0;
                restWells += p.wells();
                continue;
            }
            String words = String.format("%.0f m deep · %,d km out · %s", p.depth(), p.km(), phaseWords(p));
            String tip = String.format("%s: %d wells in its %d slots, on a field %.0f m deep and %,d km from the city. %s",
                    p.name(), p.wells(), p.slots(), p.depth(), p.km(), phaseTip(p));
            out.add(new PlatformLine(PLATFORM_COLOURS[i], p.name(), words,
                    v.counted() ? barrelsWords(p.lifted()) : "—", tip));
        }
        if (rest > 0) {
            out.add(new PlatformLine(OTHER_PLATFORMS, rest + " more", countWords(restWells, "well") + " in their slots",
                    v.counted() ? barrelsWords(restLift) : "—", "The platforms past the first " + PLATFORMS_LISTED + ", together."));
        }
        return out;
    }

    /** A platform's phase, by its oldest well: "plateau, year 2 of 3", "−8.5% a year", "no well yet". */
    static String phaseWords(Platform p) {
        if (p.oldest() < 0) return "no well yet";
        int plateau = Oil.PLATFORM_PLATEAU_MONTHS;
        if (p.oldest() < plateau) {
            return String.format("plateau, year %d of %d", p.oldest() / MONTHS_A_YEAR + 1, plateau / MONTHS_A_YEAR);
        }
        return declineWords();
    }

    /** A platform well's decline past its plateau: "−8.5% a year" (Oil.PLATFORM_KEEPS_A_YEAR). */
    static String declineWords() {
        return String.format("−%.1f%% a year", (1 - Oil.PLATFORM_KEEPS_A_YEAR) * 100);
    }

    static String phaseTip(Platform p) {
        if (p.oldest() < 0) return "No well stands in its slots yet.";
        return String.format("A platform well holds its lift for %d years, then loses %.1f%% a year; its oldest is %d months old.",
                Oil.PLATFORM_PLATEAU_MONTHS / MONTHS_A_YEAR, (1 - Oil.PLATFORM_KEEPS_A_YEAR) * 100, p.oldest());
    }

    /** The platforms' card under its lines: the offshore pool, how the crude comes ashore, and what is on site. */
    public static List<Fact> seaFacts(View v) {
        List<Fact> f = new ArrayList<>();
        f.add(new Fact("reserves left", megaTonnes(v.seaLeft()) + " · " + countWords(v.seaFields(), "field"), FACT));
        if (v.pipeKm() > 0) {
            f.add(new Fact("pipelines ashore", String.format("%,d whole · %,d km", v.pipesWhole(), v.pipeKm()), FACT));
        } else if (!v.platforms().isEmpty()) {
            f.add(new Fact("shuttle tankers, last month", money(v.shuttle()), FACT));
        }
        if (v.jacketsOnSite() > 0 || v.seaWellsOnSite() > 0) {
            f.add(new Fact("on site", (v.jacketsOnSite() > 0 ? countWords(v.jacketsOnSite(), "platform") : "")
                    + (v.jacketsOnSite() > 0 && v.seaWellsOnSite() > 0 ? " · " : "")
                    + (v.seaWellsOnSite() > 0 ? countWords(v.seaWellsOnSite(), "well") : ""), ACCENT));
        }
        return f;
    }

    /** What the platforms' card says with no platform standing. */
    public static String noPlatformWords(View v) {
        if (v.seaFields() == 0) return "The city owns no oil field in the sea.";
        if (v.shallowSites() == 0) return "The city's sea oil lies deeper than a platform stands ("
                + String.format("%.0f m", Oil.PLATFORM_MAX_DEPTH_M) + ").";
        return "None stands yet: " + String.format("%,d", v.shallowSites()) + " sea sites a platform may take.";
    }

    /** The wells' and the sea's investors' words, for the foot of the wells' box. */
    public static List<String> drillingWords(View v) {
        List<String> out = new ArrayList<>();
        if (v.drilling() != null && !v.drilling().isEmpty()) out.add("Drilling: " + v.drilling() + ".");
        if (v.atSea() != null && !v.atSea().isEmpty()) out.add("At sea: " + v.atSea() + ".");
        return out;
    }

    /** The refiners' investors' word, for the foot of the units' box. */
    public static String orderingWords(View v) {
        return v.ordering() == null || v.ordering().isEmpty() ? null : "The refiners this month: " + v.ordering() + ".";
    }

    /* ----- the units table (mockup 2's THE REFINERY) ----- */

    /**
     * A row of the units table as it is written.
     *
     * @param name    "Crude units", "Reformer"
     * @param count   "×2", or "—" with none standing
     * @param what    what it turns into what: "gas oil → petrol 55 · gas 25 · diesel 18 · fuel oil 7"
     * @param barrels its nameplate, or the size weighed: "2,000"
     * @param running "94%", "idle", "on site", "—"
     * @param spread  "+$0.0800", "—"
     * @param spreadColour its colour
     * @param state   the pill: "standing", "building", "would pay", "no feed", "no ground", "no staff", "does not pay", "would lose"
     * @param stateColour the pill's colour
     * @param words   after the pill: what stops one more, or what it would earn
     * @param tip     the whole of it, under the pointer
     */
    public record UnitLine(String name, String count, String what, String barrels, String running, String spread,
                           String spreadColour, String state, String stateColour, String words, String tip) { }

    /** The table's heads, in its columns' order (mockup 2's). */
    public static final String[] UNIT_HEADS = { "Unit · what it turns into what", "b/d", "running", "spread", "state" };

    /** The columns' widths (b/d, running, spread, the state; the first takes the rest - 238 px in the units' box's 650 at the 1,389 window). */
    public static final double[] UNIT_WIDTHS = { 0, 64, 62, 76, 210 };

    /** The pills' colours: neutral, under way, good, a gate the player can clear, a loss (mockup 2's .pill, .b, .w, .r). */
    public static final String PILL = RefineryView.TEXT_2, PILL_BUILDING = ACCENT, PILL_GOOD = GOOD, PILL_GATE = WARN,
            PILL_BAD = "#ff9e9e";

    /** The units table's rows. */
    public static List<UnitLine> unitLines(View v) {
        List<UnitLine> out = new ArrayList<>();
        for (UnitRow u : v.units()) out.add(unitLine(v, u));
        return out;
    }

    static UnitLine unitLine(View v, UnitRow u) {
        String name = u.crude() ? "Crude units" : u.kind().unitName();
        String count = u.standing() > 0 ? "×" + u.standing() : "—";
        String what = u.crude() ? "crude → its " + RefineryView.COLUMN.length + " cuts" : whatWords(u.kind());
        double feed = u.standing() > 0 ? u.feed() : u.gate() == null ? Double.NaN
                : u.crude() ? u.gate().template().uses(Good.CRUDE) * Refining.CRUDE_LITRES_PER_TONNE : u.gate().template().feedPerMonth();
        String barrels = Double.isFinite(feed) ? barrelsFigure(feed) : "—";
        String running = u.standing() > 0 ? (!Double.isFinite(u.share()) ? "—" : !(u.run() > 0) ? "idle" : percent(u.share()))
                : u.onSite() > 0 ? "on site" : "—";
        String spread = u.crude() ? "—" : RefineryView.spreadWords(u.citySpread());
        String spreadColour = u.crude() || !(u.citySpread() != 0) ? RefineryView.TEXT_3 : u.citySpread() > 0 ? GOOD : BAD;
        String[] state = stateOf(u);
        return new UnitLine(name, count, what, barrels, running, spread, spreadColour, state[0], state[1], state[2], unitTip(u));
    }

    /**
     * A kind's feed and yields, the litres largest first and coke last:
     * "residue → diesel 35 · naphtha 15 · fuel oil 12 · gas 8 · coke 30"; each
     * item's words joined by NO_BREAK, so a line wraps only between items.
     */
    public static String whatWords(Kind k) {
        List<RefineryFlow.Yield> litres = new ArrayList<>(), tonnes = new ArrayList<>();
        for (RefineryFlow.Yield y : k.yields()) (Double.isFinite(y.product().litresPerTonne()) ? litres : tonnes).add(y);
        litres.sort((a, b) -> Double.compare(b.perLitre(), a.perLitre()));
        StringBuilder s = new StringBuilder(k.feed().words().replace(' ', NO_BREAK)).append(NO_BREAK).append('→');
        boolean first = true;
        for (RefineryFlow.Yield y : litres) {
            s.append(first ? " " : " · ").append(item(RefineryView.shortName(y.product()), Math.round(y.perLitre() * 100)));
            first = false;
        }
        for (RefineryFlow.Yield y : tonnes) {
            s.append(first ? " " : " · ").append(item(RefineryView.shortName(y.product()),
                    Math.round(y.perLitre() * RefineryFlow.RESIDUE_LITRES_PER_TONNE * 100)));
            first = false;
        }
        return s.toString();
    }

    /** A nameplate in barrels a day for the table, to three figures: "2,000", "99,900", past a million "206M". */
    static String barrelsFigure(double litresAMonth) {
        double b = litresAMonth / RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY;
        return b >= 1e6 ? RefineryView.compact(b) : RefineryView.barrels(litresAMonth).replace(" b/d", "");
    }

    /** The space inside a table's item, where a line does not break: "fuel oil 7" stays whole. */
    public static final char NO_BREAK = '\u00a0';

    private static String item(String product, long percent) {
        return (product + " " + percent).replace(' ', NO_BREAK);
    }

    /** The pill, its colour and its words: {state, colour, words}. */
    static String[] stateOf(UnitRow u) {
        SpreadPlanner.Candidate c = u.gate();
        if (u.standing() > 0) return new String[] { "standing", PILL, oneMoreWords(u) };
        if (u.onSite() > 0) {
            return new String[] { "building", PILL_BUILDING, u.onSite() + " on site"
                    + (u.months() > 0 ? String.format(" · %.0f months to go", Math.ceil(u.months())) : "") };
        }
        if (c == null) return new String[] { "—", PILL, "no such building" };
        if (c.passes()) return new String[] { "would pay", PILL_GOOD, String.format("%.2f%% a month on %s", 100 * c.score(), money(c.cost())) };
        return switch (c.failed()) {
            case FEED -> new String[] { "no feed", PILL_GATE, u.crude() ? noCrudeWords(u)
                    : RefineryView.litres(u.spare()) + " spare, needs " + RefineryView.litres(SpreadPlanner.FEED_GATE * c.template().feedPerMonth()) };
            case LAND -> new String[] { "no ground", PILL_GATE, "no ground for one" };
            case STAFF -> new String[] { "no staff", PILL_GATE, "the city could not staff one" };
            case MONEY -> !u.crude() && !(u.citySpread() > 0)
                    ? new String[] { "would lose", PILL_BAD, "its products fetch too little" }
                    : new String[] { "does not pay", PILL_BAD, money(c.earns()) + " a month on " + money(c.cost()) };
        };
    }

    /** One more of a kind standing, in a few words: what stops it, or what it would earn. */
    static String oneMoreWords(UnitRow u) {
        SpreadPlanner.Candidate c = u.gate();
        if (c == null) return "";
        if (c.passes()) return String.format("another earns %.2f%% a month", 100 * c.score());
        return switch (c.failed()) {
            case FEED -> u.crude() ? noCrudeWords(u) : "no " + u.kind().feed().words() + " spare";
            case LAND -> "no ground for another";
            case STAFF -> "no staff for another";
            case MONEY -> "another would not pay";
        };
    }

    /** A crude unit refused its feed: the petrol and diesel the city is short, and the crude its wells have spare - both under the gate. */
    static String noCrudeWords(UnitRow u) {
        return "fuel short " + RefineryView.litres(u.fuelShort()) + " · crude spare " + RefineryView.tonnes(u.spare());
    }

    /** Under the pointer, a row: its buildings, its run, its spread, and one more weighed by the planner with its reason whole. */
    static String unitTip(UnitRow u) {
        StringBuilder s = new StringBuilder(u.crude() ? "Crude units" : u.kind().unitName()).append(": ")
                .append(u.standing()).append(" standing");
        if (u.onSite() > 0) s.append(", ").append(u.onSite()).append(" on site");
        if (u.crude()) {
            s.append(". They run ").append(RefineryView.barrels(u.feed())).append(" of crude");
            if (Double.isFinite(u.share()) && u.standing() > 0) s.append(", at ").append(percent(u.share())).append(" this month");
            s.append('.');
        } else {
            s.append(". Takes ").append(u.kind().feed().words()).append("; ").append(RefineryView.barrels(u.feed())).append(" of it.");
            if (Double.isFinite(u.run()) && u.standing() > 0) {
                s.append(u.run() > 0 ? " Took " + RefineryView.litres(u.run()) + ", " + percent(u.share()) + " of what it could."
                        : " Idle this month.");
            }
            s.append("\nSpread a litre of feed at the city's own prices: ").append(RefineryView.spreadWords(u.citySpread()))
                    .append(" (what its products fetch here, less what its feed would).");
            if (Double.isFinite(u.spare())) s.append(" Spare feed: ").append(RefineryView.litres(u.spare())).append(" a month.");
        }
        SpreadPlanner.Candidate c = u.gate();
        if (c != null) {
            s.append("\nOne more, ").append(c.template().getName()).append(": ");
            if (c.passes()) {
                s.append(String.format("passes every gate - it would earn %s a month on %s, %.2f%% a month.",
                        money(c.earns()), money(c.cost()), 100 * c.score()));
            } else {
                s.append(RefineryView.gateName(c.failed())).append(": ").append(c.why()).append('.');
            }
        }
        return s.toString();
    }

    /* ----- the products table (mockup 2's PRODUCTS) ----- */

    /**
     * A row of the products table as it is written.
     *
     * @param hot whether some of it was imported (the figure in the warning's colour, mockup 2's)
     */
    public record ProductLine(Good good, String colour, String name, String note, String price, String world, String ladder,
                              String made, String used, String imported, boolean hot, String exported, String where,
                              String tip) { }

    /** The table's heads, in its columns' order (mockup 2's). */
    public static final String[] PRODUCT_HEADS = { "Product", "price here", "world", "× crude", "made here", "used here",
            "imported", "exported", "where it went" };

    /** The columns' widths (the product, the seven figures; where it went takes the rest - 560 px in the box's 1,208 at the 1,389 window). */
    public static final double[] PRODUCT_WIDTHS = { 150, 76, 76, 58, 72, 72, 72, 72, 0 };

    /** The products table's line over it. */
    public static final String PRODUCTS_WORDS = "this month · litres (M a million, k a thousand); crude, bitumen and coke in"
            + " tonnes · a price is a litre's or a tonne's";

    /** ...and at its right. */
    public static final String PRODUCTS_RIGHT = "world: halfway between the import and the export price · × crude: the"
            + " world price over crude's";

    /** Crude's colour in the table (mockup 1's). */
    public static final String CRUDE = RefineryView.CRUDE;

    /** The products table's rows: crude, then the nine. */
    public static List<ProductLine> productLines(View v) {
        List<ProductLine> out = new ArrayList<>();
        for (Product p : v.products()) {
            Good g = p.good();
            boolean crude = g == Good.CRUDE;
            String name = crude ? "Crude oil" : g.label();
            out.add(new ProductLine(g, crude ? CRUDE : RefineryView.colour(g), name, crude ? "lifted here" : null,
                    RefineryView.price(p.price()), RefineryView.price(p.world()), String.format("%.2f", p.ladder()),
                    figure(g, p.made()), figure(g, p.used()), figure(g, p.imported()), p.imported() >= HALF,
                    figure(g, p.exported()), whereWords(p, v.counted()), productTip(p, v.counted())));
        }
        return out;
    }

    /** A figure in the product's unit, one that rounds to nothing as "—": "2.00M", "830k t". */
    static String figure(Good g, double units) {
        return Double.isFinite(units) && Math.abs(units) >= HALF ? RefineryView.figure(g, units) : "—";
    }

    /** Under half a unit, a figure is written as nothing (RefineryView.figure() would write "0"). */
    static final double HALF = .5;

    /**
     * Where it went: its takers here, largest first, at most
     * RefineryView.LIST_MOST - "cars 1.20M · rail 40.0k and more" - then into
     * the tanks and idled, each always named.
     */
    static String whereWords(Product p, boolean counted) {
        if (!counted) return NOT_COUNTED;
        StringBuilder s = new StringBuilder();
        int home = 0, named = 0;
        for (Taker t : p.takers()) {
            boolean kept = isKept(t);
            if (!kept && home++ >= RefineryView.LIST_MOST) continue;
            if (named++ > 0) s.append(" · ");
            s.append(lower(t.name())).append(' ').append(RefineryView.figure(p.good(), t.units()));
        }
        if (home > RefineryView.LIST_MOST) s.append(" and more");
        if (s.length() == 0) return p.exported() > 0 ? "all of it abroad" : "—";
        return s.toString();
    }

    /** Whether a taker is the tanks or the idled, not a buyer. */
    static boolean isKept(Taker t) {
        return "Into the tanks".equals(t.name()) || "Idled".equals(t.name());
    }

    /** A taker's name in a list: "Cars" is "cars"; a sector's label kept as it is ("Business Services"). */
    static String lower(String name) {
        return switch (name) {
            case "Cars", "Vans & lorries", "Rail", "Factories", "Roads", "Into the tanks", "Idled" -> name.toLowerCase();
            default -> name;
        };
    }

    static String productTip(Product p, boolean counted) {
        Good g = p.good();
        StringBuilder s = new StringBuilder(g == Good.CRUDE ? "Crude oil" : g.label()).append(": ")
                .append(RefineryView.price(p.price())).append(Double.isFinite(g.litresPerTonne()) ? " a litre" : " a tonne")
                .append(" here, ").append(RefineryView.price(p.world())).append(" the world's - ")
                .append(String.format("%.2f", p.ladder())).append(" × crude's.");
        if (counted) {
            s.append("\nMade here ").append(RefineryView.unitsWords(g, p.made())).append(", used here ")
                    .append(RefineryView.unitsWords(g, p.used())).append(" (").append(RefineryView.unitsWords(g, p.imported()))
                    .append(" of it imported), exported ").append(RefineryView.unitsWords(g, p.exported())).append('.');
            for (Taker t : p.takers()) s.append("\n").append(t.name()).append(' ').append(RefineryView.unitsWords(g, t.units()));
        }
        return s.toString();
    }

    /* ----- the strategic reserve, and its two levers ----- */

    /** The steps a lever's ladder takes from nothing to the most it may be set to: a hundredth of its reach a step (★ O12-8). */
    public static final int LADDER_STEPS = 100;

    /** The reserve's lines: what stands and holds, its book, the fill ordered, the release, the month's trades and the last settlement. */
    public static List<Fact> reserveFacts(View v) {
        Reserve r = v.reserve();
        List<Fact> f = new ArrayList<>();
        f.add(new Fact("Strategic Reserves", r.standing() + " standing · room for " + RefineryView.tonnes(r.room()), FACT));
        f.add(new Fact("Held", RefineryView.tonnes(r.held()) + (r.room() > 0 ? " · " + percent(r.held() / r.room()) + " full" : ""), FACT));
        f.add(new Fact("Its book", money(r.book()) + (r.held() > 0 ? " · " + RefineryView.price(r.book() / r.held()) + " a tonne" : ""), FACT));
        f.add(new Fact("Fill ordered", r.fill() > 0 ? RefineryView.tonnes(r.fill()) + ", bought at the next clearing" : "none", FACT));
        f.add(new Fact("Released a month", r.release() > 0 ? RefineryView.tonnes(r.release())
                + (r.held() > 0 ? String.format(" · %.1f months' worth held", r.held() / r.release()) : " · nothing left to release")
                : "none", FACT));
        f.add(new Fact("This month's trades", "bought " + RefineryView.tonnes(r.bought()) + " · sold " + RefineryView.tonnes(r.sold()), FACT));
        f.add(new Fact("Settled last strike", "paid " + money(r.paid()) + " · took in " + money(r.took()), FACT));
        return f;
    }

    /** What the reserve's section says when none stands and none is held. */
    public static final String NO_RESERVE = "No Strategic Reserve stands. Build one under Build › Industry › Oil storage: the city"
            + " fills it with crude when crude is cheap, and releases it into the refiners' market in a price shock.";

    /** The fill's reading. */
    public static String fillReading(Reserve r) {
        return r.fill() > 0 ? RefineryView.tonnes(r.fill()) + " ordered" : "none ordered";
    }

    /** ...its status: the room and what the treasury could pay, or why it can take nothing. */
    public static String fillStatus(Reserve r) {
        if (r.standing() <= 0) return "no Strategic Reserve stands to fill";
        if (!(r.roomLeft() > 0)) return "its tanks are full";
        if (!(r.fillMost() > 0)) return "the treasury has no room to pay for crude";
        String room = "room for " + RefineryView.tonnes(r.roomLeft()) + " at " + RefineryView.price(r.importPrice()) + " a tonne";
        return r.fillMost() < r.roomLeft() ? room + " · the treasury could pay for " + RefineryView.tonnes(r.fillMost()) : room;
    }

    /** The release's reading. */
    public static String releaseReading(Reserve r) {
        return r.release() > 0 ? RefineryView.tonnes(r.release()) + " a month" : "none";
    }

    /** ...its status. */
    public static String releaseStatus(Reserve r) {
        if (!(r.held() > 0)) return r.release() > 0 ? "the reserve is empty: nothing left to release" : "the reserve is empty";
        return RefineryView.tonnes(r.held()) + " held · offered to the refiners first, the rest shipped at the export price";
    }

    /** A lever's step: its reach over LADDER_STEPS, or nothing with no reach. */
    public static double step(double most) {
        return most > 0 ? most / LADDER_STEPS : 0;
    }

    /** What an effect is written in. */
    public enum Unit { TONNES, MONEY }

    /** One effect of a lever at a value: its words, before and after, and its unit. */
    public record Effect(String label, double before, double after, Unit unit) { }

    /** What a fill of `t` would do: the crude held, the room left, its cost at today's import price. */
    public static List<Effect> fillEffects(Reserve r, double t) {
        double order = Math.max(0, Math.min(t, r.fillMost()));
        return List.of(new Effect("Crude held, once bought", r.held() + r.fill(), r.held() + order, Unit.TONNES),
                new Effect("Room left in its tanks", Math.max(0, r.roomLeft() - r.fill()), Math.max(0, r.roomLeft() - order), Unit.TONNES),
                new Effect("Its cost at today's import price", r.fill() * r.importPrice(), order * r.importPrice(), Unit.MONEY));
    }

    /** What a release of `t` a month would do: a month's release, what is held after it, a month's worth at today's price here. */
    public static List<Effect> releaseEffects(Reserve r, double t) {
        double now = Math.min(r.release(), r.held()), then = Math.min(Math.max(0, t), r.held());
        return List.of(new Effect("Released next month", now, then, Unit.TONNES),
                new Effect("Held after it", r.held() - now, r.held() - then, Unit.TONNES),
                new Effect("A month's worth at today's price here", now * r.localPrice(), then * r.localPrice(), Unit.MONEY));
    }
}
