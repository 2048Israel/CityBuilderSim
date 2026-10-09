package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One build card's figures, for every one of the 101 buildings (0.7.25): what
 * it gives the city and in what unit, its price per unit of that, the scarce
 * resource it takes, the group it is compared within and where in that group
 * it stands, what one costs to run, the verdict on an order of it - and, for
 * the buildings investors put up, what they last decided and the first gate
 * this one fails for them now.
 *
 * WHY. Jerus, after seeing 0.7.24: "also the build card for every building,
 * i think the card itself needs a redesign dont you think?" The city's five
 * categories had a card that answered "which of these, and what does it give
 * me"; the market's nine kept the 0.7.21 card, a name, two prices and a
 * stepper. One card for all 73 now (the project's design note for 0.7.25,
 * from the design study card-spec-0725): a hero that says what the building
 * gives the city, a money bar, a bar for the category's scarce resource, the
 * investors' line, what it needs, and the order. The figures moved out of
 * the screen into this class so a harness (BuildCardCheck) can hold them
 * without the toolkit; the screen keeps the words that carry money (it
 * formats money the way every screen does) and the layout.
 *
 * Pure: it reads the city and changes nothing. Every figure is a model read,
 * or a ratio of two, and each is named where it is made below.
 */
public final class BuildCard {

    private BuildCard() { }

    /* =====================================================================
       WHAT KIND OF CARD

       The city's five keep the 0.7.24 card's measure (CITY): what one serves
       at today's staffing, in the picked ring's unit. The market's nine are
       measured in what they give the city - residents, customers, meals,
       tonnes across the boundary, building points - or, for every maker,
       farm, mine and vehicle plant alike, in VALUE ADDED: what one makes
       less what it uses, at today's prices (valueAdded()). The goods differ
       inside most of those groups (bread and bakery goods; crops, dairy and
       meat; cars, vans and wagon sets), and the groups where they do not -
       steel, iron, building materials - are measured the same way. Offices
       are measured in what their exports fetch.
       ===================================================================== */

    /**
     * CITY: the picked ring's measure. HOMES: residents. CUSTOMERS: customers
     * a month (groceries and luxury counters). MEALS: meals a month. BRANCH:
     * the bank's customers per branch. RAIL: tonnes a month across the
     * boundary. POINTS: building points a month. MAKER: value added.
     * OFFICE: the value of its exports. PUMP (0.7.83): litres of petrol a
     * month. TANKS (0.7.85): litres of tank room - the refiners' Tank Farm
     * and the city's Strategic Reserve. BERTHS (0.7.86): a sea terminal's
     * tonnes a year of its kind of cargo - the city's. SLOTS (0.7.91): an
     * offshore platform's wells' slots. PIPE (0.7.91): a kilometre of crude
     * pipeline.
     */
    public enum Kind { CITY, HOMES, CUSTOMERS, MEALS, BRANCH, RAIL, POINTS, MAKER, OFFICE, PUMP, TANKS, BERTHS, SLOTS, PIPE }

    /**
     * What bar 2 measures: LAND per unit (the market's scarce resource),
     * UNFILLED posts per 10,000 served (the city's, Jerus's pick in 0.7.24),
     * UNSTAFFABLE posts of 100 (offices, whose three cards differ only in who
     * they employ), or NONE (a city building with no posts).
     */
    public enum Bar2 { LAND, UNFILLED, UNSTAFFABLE, NONE }

    /**
     * One card's figures.
     *
     * @param group      the heading it is compared under (group())
     * @param verb       the words before the hero's figure: "houses ", "makes "
     * @param figure     the hero's figure: residents, customers, a maker's first good in that good's
     *                   own unit (tonnes, kilograms, cars), an office's seat-months
     * @param words      the words after it: " residents", " t of steel a month"
     * @param unit       what the bars are per: the hero's figure, or value added for a maker and an
     *                   office, or what a city building serves at today's staffing
     * @param built      a city building's figure fully staffed (BuildAdvice.built()); NaN for the market
     * @param valueAdded a maker's or an office's value added a month (valueAdded()); NaN for the rest
     * @param good       a maker's or an office's hero good, the first it makes; null for the rest
     * @param price      the quote at one, all in (Game.quoteBuild(t, 1).total)
     * @param sticker    ...and its sticker (.sticker)
     * @param bar1       the money bar: the price per unit (x 1,000 for meals), or months of what it adds
     * @param bar2       the scarce resource's bar, by bar2Kind
     * @param per        the noun the bars are per, for a per-unit card: "resident", "1,000 meals a month"
     * @param perTag     ...and the tag's: "1,000 meals"
     * @param staffable  the share of its posts the owner's staffing test says the city's spare workers
     *                   could fill (Sector.staffing()); NaN for a city building
     * @param running    what one costs to run a month (runningCost())
     * @param detail     the detail line's figures, by kind: HOMES {homes, home size, 1 when adults only};
     *                   BRANCH {the capital a branch brings}; POINTS {the builders' output a month};
     *                   OFFICE {the price of a seat-month}; a mine {deposits owned, mines committed,
     *                   tonnes in the ground}; otherwise empty
     * @param owned      how many stand (BuildingManager.getQuantity())
     * @param onSite     how many are on site, for anyone's order
     * @param siteMonths ...and their wait at today's queue (Game.onSiteMonths()); 0 with none
     * @param landFree   the land free, for the needs line's share of it (LandManager.getAvailableSqFt())
     */
    public record Figures(BuildingsTemplate template, Kind kind, String group,
                          String verb, double figure, String words, double unit,
                          double built, double valueAdded, Good good,
                          double price, double sticker,
                          double bar1, double bar2, Bar2 bar2Kind,
                          String per, String perTag,
                          double staffable, double running, double[] detail,
                          int owned, int onSite, double siteMonths, double landFree) {

        /** No positive unit to price against: a maker whose inputs cost more than it makes, or a city building nobody could staff. Both bars read "—". */
        public boolean addsNothing() { return !(unit > 0); }

        /** Measured in months of what it adds or exports, not a price per unit. */
        public boolean inMonths() { return kind == Kind.MAKER || kind == Kind.OFFICE; }

        /** Built by investors as well as by the city - not the city's own Strategic Reserve (0.7.85) or sea terminals (0.7.86), which only the city builds. */
        public boolean market() { return kind != Kind.CITY && kind != Kind.BERTHS && !StrategicReserve.isReserve(template); }
    }

    /* =====================================================================
       THE CITY'S WORDS (0.7.24, moved here from the screen in 0.7.25)

       What a city card says one does, in the measure's own verb - "generates
       N kW" (0.7.28; "makes N units a month" until then), "makes N units a
       month" of water, "carries N trips a month", "serves N people", "seats N
       children", "puts N officers on the street", "holds N prisoners" (after
       the 0.7.24 PC check: "serves 113 officers" and "serves 900 trips a
       month" read wrongly) - and the one of it a cost is per. Death care has
       two kinds of building: a cemetery's capacity is plots, which the model
       counts whole; a crematorium's is a month's cremations.
       ===================================================================== */

    /**
     * The words after a power plant's figure (0.7.28): kilowatts, the
     * model's unit and a rate - "units a month" until then, which was
     * neither. A screen writes the figure scaled with its unit (Money.power():
     * "8.1 MW") and leaves these off.
     */
    public static final String KILOWATTS = " kW";

    /** The words before the figure and after it, for a city building on a measure. */
    public static String[] doesWords(BuildAdvice.Measure m, BuildingsTemplate t) {
        switch (m.kind()) {
            case POWER:   return new String[] {"generates ", KILOWATTS};
            case WATER:   return new String[] {"makes ", " units a month"};
            case ROADS:   return new String[] {"carries ", " trips a month"};
            case TRANSIT: return new String[] {"carries ", " riders"};
            case CARE:    return new String[] {"serves ", m.care() == CareType.CHILDCARE ? " children"
                                                        : m.care() == CareType.SENIOR ? " seniors" : " people"};
            case DEATH: case PLOTS:
                return t.getCare() == CareType.BURIAL ? new String[] {"has ", " plots"}
                                                      : new String[] {"cremates ", " a month"};
            case SCHOOL:  return new String[] {"seats ", m.school().isBasic() ? " children" : " students"};
            case POLICE:  return new String[] {"puts ", " officers on the street"};
            default:      return new String[] {"holds ", " prisoners"};
        }
    }

    /** ...the one of it a cost is per: "cost per patient", "cheapest per child". */
    public static String perWords(BuildAdvice.Measure m) {
        switch (m.kind()) {
            case POWER:   return "kW";
            case WATER:   return "unit";
            case ROADS:   return "trip";
            case TRANSIT: return "rider";
            case CARE:    return m.care() == CareType.CHILDCARE ? "child" : m.care() == CareType.SENIOR ? "senior" : "patient";
            case DEATH: case PLOTS: return "body";
            case SCHOOL:  return m.school().isBasic() ? "child" : "student";
            case POLICE:  return "officer";
            default:      return "prisoner";
        }
    }

    /** ...and many of it, for the staff bar's "per 10,000 patients". */
    public static String perPlural(BuildAdvice.Measure m) {
        switch (m.kind()) {
            case POWER:   return "kW";
            case WATER:   return "units";
            case ROADS:   return "trips";
            case TRANSIT: return "riders";
            case CARE:    return m.care() == CareType.CHILDCARE ? "children" : m.care() == CareType.SENIOR ? "seniors" : "patients";
            case DEATH: case PLOTS: return "bodies";
            case SCHOOL:  return m.school().isBasic() ? "children" : "students";
            case POLICE:  return "officers";
            default:      return "prisoners";
        }
    }

    /**
     * What one serves at today's staffing, in the measure's own unit
     * (BuildAdvice.unit()); for road capacity the road's own capacity, since
     * BuildAdvice.unit() is the advice's trips relieved.
     */
    public static double served(Game game, BuildAdvice.Measure m, BuildingsTemplate t) {
        if (m.kind() == BuildAdvice.Kind.ROADS) return t.getCapacity();
        return BuildAdvice.unit(game, m, t);
    }

    /** The first ring of its category a city building serves - its group on a page that has none picked. */
    public static BuildAdvice.Measure cityMeasure(BuildingsTemplate t) {
        BuildAdvice.Category c = BuildAdvice.categoryOf(t.getCategory());
        if (c == null || !c.cityBuilds()) return null;
        for (BuildAdvice.Measure m : BuildAdvice.measuresOf(c.name())) if (m.serves(t)) return m;
        return null;
    }

    /* =====================================================================
       THE MARKET'S GROUPS

       On a market page the cards sit under their owning sector, so Industry
       has nine groups and Shops two, and the bars and tags compare within a
       group: a steel mill against a steel mill, not against a bakery. The
       names are the design note's, and since 0.7.62 the wells' and the
       refinery's ("Oil", "Refining") after the mines'.
       ===================================================================== */

    /** The market groups, in the order a page lays them out. */
    static final String[] GROUP_ORDER = {
            "Homes", "Groceries", "Filling stations", "The bank's branches",
            "Food mills", "Food processing", "Steel", "Fabrication & machinery", "Iron", "Oil", "Oil platforms",
            "Oil pipelines", "Refining",
            "Oil storage", "Ports", "Building materials", "Builders",
            "Offices", "Farms", "Rail", "Vehicles", "Luxury shops", "Restaurants" };

    /**
     * The heading a building is compared under: a market building's group by
     * its owning sector, or, for a city building, the first ring of its
     * category it serves.
     */
    public static String group(BuildingsTemplate t) {
        BuildAdvice.Category c = BuildAdvice.categoryOf(t.getCategory());
        if (c == null) return t.getCategory().name();
        // ...the refiners' Tank Farm and the city's Strategic Reserve together, measured in tank room (0.7.85).
        if (kindOf(t) == Kind.TANKS) return "Oil storage";
        // ...and the city's sea terminals, measured in their berths' tonnes (0.7.86).
        if (kindOf(t) == Kind.BERTHS) return "Ports";
        // ...and the oil at sea's platforms, in their wells' slots, and pipelines, in kilometres (0.7.91): the wells stay with the land's.
        if (kindOf(t) == Kind.SLOTS) return "Oil platforms";
        if (kindOf(t) == Kind.PIPE) return "Oil pipelines";
        if (c.cityBuilds()) {
            BuildAdvice.Measure m = cityMeasure(t);
            return m == null ? c.name() : m.label();
        }
        switch (c.name()) {
            case BuildAdvice.SHOPS:
                // ...and the grocers' forecourts (0.7.83): their own group, measured in litres.
                return !t.isOwnedBySector() ? "The bank's branches"
                        : ham.citybuildersim.sectors.Retail.isStation(t) ? "Filling stations" : "Groceries";
            case BuildAdvice.INDUSTRY:
                switch (t.getSector()) {
                    case Sectors.INDUSTRY:        return "Food mills";
                    case Sectors.FOOD_PROCESSING: return "Food processing";
                    case Sectors.HEAVY_INDUSTRY:  return "Steel";
                    case Sectors.MANUFACTURING:   return "Fabrication & machinery";
                    case Sectors.MINING:          return "Iron";
                    case Sectors.OIL:             return "Oil";
                    case Sectors.REFINING:        return "Refining";
                    case Sectors.MATERIALS:       return "Building materials";
                    case Sectors.CONSTRUCTION:    return "Builders";
                    default:                      return t.getSector();
                }
            default:
                return c.name();
        }
    }

    /**
     * The group a sector's own buildings are compared under on Build
     * (0.7.30): its first own building's - "Food mills" for the sector called
     * Industry, which bakes. The Sectors list orders its cards by it and
     * names it under each sector's name; null with no building of its own.
     */
    public static String groupOf(Game game, Sector sector) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplatesBySector(sector.key())) {
            if (t.isOwnedBySector() && !citys(t)) return group(t);
        }
        return null;
    }

    /** A building only the city builds and nobody's investors weigh: a city category's, and the city's Strategic Reserve (0.7.85) and sea terminals (0.7.86) on a market page. */
    public static boolean citys(BuildingsTemplate t) {
        return kindOf(t) == Kind.CITY || StrategicReserve.isReserve(t) || Ports.isPort(t);
    }

    /** ...and the Build category that group is on: "Industry"; null with none. */
    public static String categoryOf(Game game, Sector sector) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplatesBySector(sector.key())) {
            if (!t.isOwnedBySector() || citys(t)) continue;
            BuildAdvice.Category c = BuildAdvice.categoryOf(t.getCategory());
            return c == null ? null : c.name();
        }
        return null;
    }

    /** Where a market group sits in Build's order (GROUP_ORDER); after them all for one not in it. */
    public static int groupRank(String group) {
        for (int i = 0; i < GROUP_ORDER.length; i++) if (GROUP_ORDER[i].equals(group)) return i;
        return GROUP_ORDER.length;
    }

    /** What a building is measured in (see Kind). */
    public static Kind kindOf(BuildingsTemplate t) {
        BuildAdvice.Category c = BuildAdvice.categoryOf(t.getCategory());
        if (c == null || c.cityBuilds()) return Kind.CITY;
        if (t.getCategory() == BuildingType.RESIDENTIAL) return Kind.HOMES;
        if (t.getCategory() == BuildingType.BUSINESS_SERVICES) return Kind.OFFICE;
        if (t.getCategory() == BuildingType.COMMERCIAL && !t.isOwnedBySector()) return Kind.BRANCH;
        if (ham.citybuildersim.sectors.Retail.isStation(t)) return Kind.PUMP;
        if (ham.citybuildersim.sectors.Refining.isTankFarm(t) || StrategicReserve.isReserve(t)) return Kind.TANKS;
        if (Ports.isPort(t)) return Kind.BERTHS;
        if (t.isPlatform()) return Kind.SLOTS;
        if (t.isPipeline()) return Kind.PIPE;
        if (t.getRailCapacity() > 0) return Kind.RAIL;
        if (t.makes(Good.MEALS) > 0) return Kind.MEALS;
        if (t.makes(Good.GROCERIES) > 0 || t.makes(Good.LUXURY_TRADE) > 0) return Kind.CUSTOMERS;
        if (t.makes(Good.BUILDING_WORK) > 0) return Kind.POINTS;
        return Kind.MAKER;
    }

    /* =====================================================================
       VALUE ADDED

       Σ made x P - Σ used x P over the goods with a market price, P the
       market's local price today (GoodsMarket.getLocalPrice()) - except an
       office's work, priced at what the world pays for a seat-month in the
       city's money (BusinessServices.priceOfSeat(), the net export price,
       which is what that sector is paid). Seller-priced goods (groceries,
       homes, building work, a counter's trade, meals) carry no market price
       and are left out; none is in a value-added group.

       WHY VALUE ADDED, not the investors' profit: the goods differ inside the
       groups, so no physical unit compares them, and value added is what one
       adds to the city's output at prices the model already holds. The
       owner's figure comes after wages, at the sector's operating rate
       (BusinessInvestment.estimatedMakerProfit()); in the design study's city
       it was negative for most makers while their value added was positive.
       It is on the investors' line and the (i), not on the city's bar.
       ===================================================================== */

    /** The price a good is valued at for value added, or NaN for one with no market price. */
    public static double priceOf(Game game, Good g) {
        if (g == null || !g.traded()) return Double.NaN;
        if (game.getSectors().businessServices().isMaker(g)) return game.getSectors().businessServices().priceOfSeat(g);
        return game.getMarkets().get(g).getLocalPrice();
    }

    /**
     * What one makes less what it uses a month, at today's prices - what it
     * makes being a crude unit's slate since 0.7.76 (sectors.Refining.madeBy()),
     * its template naming only the crude. A conversion unit (0.7.80) uses no
     * good: what it makes of its feed is less what the feed would have made
     * with no unit (Refining.feedValueOf()), its spread on the whole feed.
     */
    public static double valueAdded(Game game, BuildingsTemplate t) {
        double v = 0;
        for (Map.Entry<Good, Double> e : ham.citybuildersim.sectors.Refining.madeBy(t).entrySet()) {
            double p = priceOf(game, e.getKey());
            if (Double.isFinite(p)) v += e.getValue() * p;
        }
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            double p = priceOf(game, e.getKey());
            if (Double.isFinite(p)) v -= e.getValue() * p;
        }
        if (ham.citybuildersim.sectors.Refining.isConversionUnit(t)) {
            v -= ham.citybuildersim.sectors.Refining.feedValueOf(t,
                    ham.citybuildersim.sectors.RefineryFlow.values(g -> priceOf(game, g)));
        }
        return v;
    }

    /** A conversion unit's feed in words, after its figure (0.7.80): " litres of heavy naphtha a month". */
    public static String feedWords(BuildingsTemplate t) {
        return " litres of " + t.refineryUnit().feed().words() + " a month";
    }

    /**
     * A good after its count: " t of steel", " kg of bread", " units of
     * building materials", " seat-months of support work" - and for a good
     * counted in itself, " cars", " vans and trucks", " wagon sets of rolling
     * stock".
     */
    public static String goodWords(Good g) {
        String unit = g.unit(), label = g.label().toLowerCase();
        if (unit.equals("tonne")) return " t of " + label;
        String units = Formats.plural(unit);
        if (label.startsWith(units)) return " " + label;
        return " " + units + " of " + label;
    }

    /** The verb a maker's hero opens on: a farm grows, a mine lifts, the rest make. */
    static String makerVerb(BuildingsTemplate t) {
        if (t.getCategory() == BuildingType.AGRICULTURE) return "grows ";
        if (t.getCategory() == BuildingType.MINING) return "lifts ";
        return "makes ";
    }

    /** The good a maker's or an office's hero names: the first it makes. */
    static Good firstGood(BuildingsTemplate t) {
        for (Good g : t.goodsMade().keySet()) return g;
        return null;
    }

    /* =====================================================================
       ONE CARD
       ===================================================================== */

    /** What a road card's first bar is per (0.7.70): a trip it takes off the road, over its life, its ground in it - BuildAdvice.lifetime() over BuildAdvice.unit(); the bar's (i) says the rest. */
    public static final String ROAD_PER = "trip over " + BuildAdvice.LIFE_MONTHS / 12 + " years, with its land";

    /**
     * PAVING, ON THE GRAVEL ROAD'S CARD (0.7.70; ConstructionControl, F):
     * the gravel roads the city could pave and those being paved, the quote
     * for one (Game.quotePave()), the trips one paving takes off the road
     * (BuildAdvice.pavingUnit()) and the ground it frees, its cost over its
     * life a trip (BuildAdvice.pavingLifetime() over the unit) beside a new
     * Paved Road's (the road card's own bar 1), and whether it beats it -
     * when the card and the advice recommend it (BuildAdvice.pavingBeatsPaved()).
     * Null for any other building, or with no Paved Road in the catalogue.
     */
    public record Paving(int paveable, int paving, Game.BuildQuote one, double unit, double frees,
                         double perTrip, double pavedPerTrip, boolean beatsPaved) { }

    public static Paving paving(Game game, BuildingsTemplate t) {
        if (!ConstructionControl.paves(t)) return null;
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        Game.BuildQuote one = game.quotePave(1);
        if (to == null || one == null) return null;
        BuildAdvice.Measure roads = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        java.util.Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(game, roads);
        double free = game.getLandManager().getAvailableSqFt();
        double unit = BuildAdvice.pavingUnit(game, site), paved = BuildAdvice.unit(game, roads, to);
        double per = unit > 0 ? BuildAdvice.pavingLifetime(game, free, site) / unit : Double.POSITIVE_INFINITY;
        double pavedPer = paved > 0 ? BuildAdvice.lifetime(game, to, free, site) / paved : Double.POSITIVE_INFINITY;
        return new Paving(game.paveable(), game.pavingNow(), one, unit, BuildAdvice.pavingFrees(game), per, pavedPer,
                BuildAdvice.pavingBeatsPaved(game, free, site));
    }

    /** What one costs to keep a month, which is not what it costs to buy: its upkeep and its posts at today's wages (PopulationManager.getWagesPerType()). */
    public static double runningCost(Game game, BuildingsTemplate t) {
        double[] wages = game.getPopulationManager().getWagesPerType();
        double bill = 0;
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
            if (n == 0) continue;
            if (wages != null && job.ordinal() < wages.length) bill += n * wages[job.ordinal()];
        }
        return t.getUpkeep() + bill;
    }

    /** The sector whose money and staffing test a market building is judged by: its owner, or retail for the bank's branch (planBank() asks retail). */
    public static Sector ownerOf(Game game, BuildingsTemplate t) {
        Sector owner = game.getSectors().ownerOf(t);
        return owner != null ? owner : game.getSectors().retail();
    }

    /**
     * One card's figures. A city building on the measure its page has picked
     * (m), or with m null its first ring; a market building with m ignored.
     */
    public static Figures of(Game game, BuildingsTemplate t, BuildAdvice.Measure m) {
        Kind kind = kindOf(t);
        Game.BuildQuote one = game.quoteBuild(t, 1);
        double price = one.total, sticker = one.sticker;
        double running = runningCost(game, t);
        double land = t.getLandSqFt();
        double free = game.getLandManager().getAvailableSqFt();
        BuildingManager bm = game.getBuildingManager();
        int owned = bm.getQuantity(t.getId());
        BuildingsStacks stack = bm.getStack(t);
        int onSite = stack == null ? 0 : stack.getUnderConstruction();
        double siteMonths = onSite > 0 ? game.onSiteMonths(t) : 0;

        if (kind == Kind.CITY) {
            BuildAdvice.Measure on = m != null && m.serves(t) ? m : cityMeasure(t);
            if (on == null) on = m;
            String[] does = doesWords(on, t);
            double served = served(game, on, t);
            double[] fill = game.getPopulationManager().getJobFillRate();
            boolean posts = BuildAdvice.hasPosts(t);
            if (on.kind() == BuildAdvice.Kind.ROADS) {
                // A ROAD, OVER ITS LIFE (0.7.70): the advice's own figure - its
                // cost over its life with its ground (BuildAdvice.lifetime()) a
                // trip it takes off the road (BuildAdvice.unit(), its freight
                // grade in it) - and its ground a trip, where a road's price a
                // trip carried tagged the gravel road the cheapest in every city.
                double off = BuildAdvice.unit(game, on, t);
                double bar1 = off > 0 ? BuildAdvice.lifetime(game, t, free, BuildAdvice.onSite(game, on)) / off
                        : Double.POSITIVE_INFINITY;
                double bar2 = off > 0 ? land / off : Double.POSITIVE_INFINITY;
                return new Figures(t, kind, on.label(), does[0], served, does[1], served,
                        BuildAdvice.built(game, on, t), Double.NaN, null, price, sticker,
                        bar1, bar2, Bar2.LAND, ROAD_PER, perWords(on),
                        Double.NaN, running, new double[0], owned, onSite, siteMonths, free);
            }
            // The cost per unit served at today's staffing, and the posts the
            // city likely cannot fill per 10,000 of it (0.7.24, Jerus's pick):
            // infinite for one nobody could staff, none for one with no posts.
            double bar1 = served > 0 ? price / served : Double.POSITIVE_INFINITY;
            double bar2 = !posts ? Double.NaN
                    : served > 0 ? BuildAdvice.unfilledPosts(t, fill) * 10_000.0 / served : Double.POSITIVE_INFINITY;
            return new Figures(t, kind, on.label(), does[0], served, does[1], served,
                    BuildAdvice.built(game, on, t), Double.NaN, null, price, sticker,
                    bar1, bar2, posts ? Bar2.UNFILLED : Bar2.NONE, perWords(on), perWords(on),
                    Double.NaN, running, new double[0], owned, onSite, siteMonths, free);
        }

        double staffable = ownerOf(game, t).staffing(t).share;
        String group = group(t);
        String verb, words, per = null, perTag = null;
        double figure, unit, scale = 1, va = Double.NaN;
        Good good = null;
        double[] detail = new double[0];
        switch (kind) {
            case HOMES:
                verb = "houses "; words = " residents"; figure = unit = t.getCapacity();
                per = perTag = "resident";
                detail = new double[] { Math.max(t.getDwellings(), 1), t.homeSize(), t.adultsOnly() ? 1 : 0 };
                break;
            case CUSTOMERS:
                verb = "serves "; words = " customers a month"; figure = unit = t.getCoverage();
                per = perTag = "customer";
                break;
            case MEALS:
                verb = "serves "; words = " meals a month"; figure = unit = t.getCoverage();
                per = "1,000 meals a month"; perTag = "1,000 meals"; scale = 1000;
                break;
            case BRANCH:
                verb = "a branch for "; words = " customers"; figure = unit = Bank.CUSTOMERS_PER_BRANCH;
                per = perTag = "customer";
                detail = new double[] { Bank.PAID_IN_PER_BRANCH };
                break;
            case PUMP: {
                // A filling station (0.7.83): the litres it sells a month at its typical throughput, and the pump
                // price on today's wholesale.
                verb = "sells "; words = " L of petrol a month"; figure = unit = t.pumpLitres();
                per = "1,000 L a month"; perTag = "1,000 L"; scale = 1000;
                ham.citybuildersim.sectors.Retail grocers = game.getSectors().retail();
                double litre = game.getMarkets().get(Good.PETROL).landedPrice();
                detail = new double[] { grocers.pumpPriceToday(game), Double.isFinite(litre) ? litre : 0 };
                break;
            }
            case RAIL:
                verb = "hauls up to "; words = " t a month across the boundary"; figure = unit = t.getRailCapacity();
                per = perTag = "tonne a month";
                break;
            case TANKS: {
                // Oil storage (0.7.85): its tank room in litres, and what is in store now against what may be -
                // the refiners' crude kept against crudeKept(), the city's reserve against its room, in tonnes.
                boolean city = StrategicReserve.isReserve(t);
                verb = "holds "; words = city ? " L of the city's crude" : " L of the refiners' crude and products";
                figure = unit = t.stocks(Good.CRUDE);
                per = "1,000 L of room"; perTag = "1,000 L"; scale = 1000;
                ham.citybuildersim.sectors.Refining refiners = game.getSectors().refining();
                detail = city ? new double[] { game.getReserve().getTonnes(), StrategicReserve.room(game.getBuildingManager()) }
                        : new double[] { refiners.getPantry(Good.CRUDE), refiners.crudeKept() };
                break;
            }
            case BERTHS: {
                // A sea terminal (0.7.86): its berth's tonnes a year of its kind, and the kind's month - what could go
                // by sea, the berths standing, and the share at sea in force (Ports).
                Ports.Cargo k = t.berthCargo();
                Ports ports = game.getPorts();
                verb = "handles up to "; words = " t a year of " + k.label().toLowerCase();
                figure = unit = t.berthTonnesAYear();
                per = "1,000 t a year"; perTag = "1,000 t"; scale = 1000;
                detail = new double[] { ports.carriable(k), Ports.berths(game.getBuildingManager())[k.ordinal()], ports.share(k) };
                break;
            }
            case SLOTS: {
                // An offshore platform (0.7.91): its wells' slots, and the city's shallow sea sites - owned, slotted by the
                // platforms standing - and the wells in their slots (sectors.Oil, THE OIL AT SEA).
                verb = "holds up to "; words = " wells on a shallow sea field"; figure = unit = t.platformSlots();
                per = perTag = "well's slot";
                ham.citybuildersim.sectors.Oil wells = game.getSectors().oil();
                int sites = 0;
                for (LandManager.SeaField s : wells.shallowFields()) sites += s.sites();
                detail = new double[] { sites, wells.slotsStanding(), wells.platformWellsInSlots() };
                break;
            }
            case PIPE: {
                // A kilometre of crude pipeline (0.7.91): the kilometres standing, the fields whose whole pipe stands, and the
                // shuttle tankers' tonnes this month (sectors.Oil, THE OIL AT SEA).
                verb = "lays "; words = " km of pipe from a sea field ashore"; figure = unit = 1;
                per = perTag = "km";
                ham.citybuildersim.sectors.Oil wells = game.getSectors().oil();
                long[] at = { game.getCityLand().siteX(), game.getCityLand().siteY() };
                detail = new double[] { wells.pipeKmStanding(),
                        ham.citybuildersim.sectors.Oil.pipedFields(wells.pipelinesNow(), wells.pipeKmStanding(),
                                wells.shallowFields(), at[0], at[1]).size(),
                        wells.getShuttleTonnes() };
                break;
            }
            case POINTS:
                verb = "adds "; words = " building points a month"; figure = unit = t.makes(Good.BUILDING_WORK);
                per = perTag = "point a month";
                detail = new double[] { game.getConstructionOutput() };
                break;
            case OFFICE:
                good = firstGood(t);
                verb = "exports "; words = (good == null ? "" : goodWords(good)) + " a month";
                figure = good == null ? 0 : t.makes(good);
                va = valueAdded(game, t); unit = va;
                detail = new double[] { priceOf(game, good) };
                break;
            default:
                good = firstGood(t);
                verb = makerVerb(t); words = (good == null ? "" : goodWords(good)) + " a month";
                figure = good == null ? 0 : t.makes(good);
                // ...a crude unit (0.7.76) by the crude it refines: what it makes is the slate of it.
                if (ham.citybuildersim.sectors.Refining.isCrudeUnit(t)) {
                    good = Good.CRUDE;
                    verb = "refines "; words = goodWords(good) + " a month";
                    figure = t.uses(good);
                }
                // ...and a conversion unit (0.7.80) by the feed it upgrades: a stream of the crude units' run, no good.
                if (ham.citybuildersim.sectors.Refining.isConversionUnit(t)) {
                    verb = "upgrades "; words = feedWords(t);
                    figure = t.feedPerMonth();
                }
                va = valueAdded(game, t); unit = va;
                Resource site = Game.siteOf(t);
                if (site != null) {
                    // ...the sites of its own resource (0.7.62): an Oil Well's oil, a mine's iron - a land well's dry ones (0.7.84,
                    // Game.sitesFor()), a platform's well its platforms' slots (0.7.91), counted against its own kind (committedFor()).
                    LandManager ground = game.getLandManager();
                    detail = new double[] { game.sitesFor(t), game.committedFor(t), ground.getRemaining(site) };
                }
        }

        double bar1, bar2;
        Bar2 kind2;
        if (kind == Kind.OFFICE) {
            // Its price in months of its exports, and the posts the city
            // could not staff of every 100: an office's three cards cost the
            // same and use the same ground per seat, and differ in who they employ.
            bar1 = unit > 0 ? price / unit : Double.NaN;
            bar2 = unit > 0 ? (1 - staffable) * 100 : Double.NaN;
            kind2 = Bar2.UNSTAFFABLE;
        } else {
            // The price per unit, and the land per unit: in a city built over
            // land is the refusal both the city's order and the investors meet
            // (buildStack()'s NO_LAND, the planners' Decision.noLand()). A maker's
            // unit is a thousand dollars of value added a month, so its second
            // bar reads "land per $1k it adds a month".
            bar1 = unit > 0 ? price / unit * scale : Double.NaN;
            bar2 = unit > 0 ? land / unit * scale : Double.NaN;
            kind2 = Bar2.LAND;
        }
        return new Figures(t, kind, group, verb, figure, words, unit, Double.NaN, va, good, price, sticker,
                bar1, bar2, kind2, per, perTag, staffable, running, detail, owned, onSite, siteMonths, free);
    }

    /* =====================================================================
       A GROUP OF CARDS, SCALED AND TAGGED

       The bars are scaled to the longest in the group, and a group of one
       draws its two figures with no bar track - one card's full bar compares
       nothing. A tag goes to the lowest figure on a bar - the cheapest, the
       least land, the most added per dollar - only in a group of two or
       more, and only when it is strictly below the highest.
       ===================================================================== */

    /**
     * A note under a group's heading, by kind: DOORS {studios' households
     * without a door, families'} (RealEstate.doorShortfall(); a negative is
     * doors to spare); SHOPS {the shops' coverage a month, the people};
     * MADE {the city's demand for the planning good this month, the sector's
     * nameplate} with the good; PUMP {the drivers' litres last month, what the
     * filling stations could sell} (0.7.83); RAIL {the city's trade in tonnes, the
     * network's capacity}; LUXURY {customers who came, the counters'
     * coverage}; MEALS {meals wanted, the kitchens' seats}; NONE.
     *
     * The customers who came and the meals wanted are the month's flows,
     * which the save does not carry: until a month has run since the city
     * was loaded (the sector has no word for the month yet) they read zero,
     * so the note carries NaN for them instead - not counted yet - and says
     * only what the counters or the kitchens serve.
     */
    public enum NoteKind { DOORS, SHOPS, MADE, RAIL, LUXURY, MEALS, PUMP, NONE }

    public record Note(NoteKind kind, double a, double b, Good good) { }

    /** One group: its heading, its cards in catalogue order, and its note. */
    public record Group(String title, List<Figures> cards, Note note) {

        /** More than one card, so the bars have something to compare and draw a track. */
        public boolean track() { return cards.size() > 1; }

        public double most1()  { return extreme(true, true); }
        public double least1() { return extreme(true, false); }
        public double most2()  { return extreme(false, true); }
        public double least2() { return extreme(false, false); }

        private double extreme(boolean first, boolean most) {
            double out = Double.NaN;
            for (Figures f : cards) {
                double v = first ? f.bar1() : f.bar2();
                if (!Double.isFinite(v)) continue;
                if (Double.isNaN(out) || (most ? v > out : v < out)) out = v;
            }
            return out;
        }

        /** How much of the track a card's first bar fills: its figure over the group's largest; full for an infinite one, none for one with none. */
        public double share1(Figures f) { return share(f.bar1(), most1()); }
        public double share2(Figures f) { return share(f.bar2(), most2()); }

        private static double share(double v, double most) {
            if (Double.isInfinite(v)) return 1;
            if (!Double.isFinite(v) || !(most > 0)) return 0;
            return v / most;
        }

        /** The tag on bar 1: the strict best of two or more. */
        public boolean best1(Figures f) { return best(f.bar1(), least1(), most1()); }
        public boolean best2(Figures f) { return best(f.bar2(), least2(), most2()); }

        private boolean best(double v, double least, double most) {
            return cards.size() > 1 && Double.isFinite(v) && v == least && least < most;
        }
    }

    /**
     * A page's groups. A city category is one group, the picked ring's
     * buildings (m); a market category (m null) is its owning sectors' groups
     * in GROUP_ORDER, each with its cards in catalogue order and its note.
     */
    public static List<Group> groups(Game game, String category, BuildAdvice.Measure m) {
        BuildAdvice.Category c = BuildAdvice.category(category);
        List<Group> out = new ArrayList<>();
        if (c == null) return out;
        List<BuildingsTemplate> all = game.getBuildingManager().getTemplatesByCategory(c.types());
        if (c.cityBuilds()) {
            BuildAdvice.Measure on = m != null ? m : BuildAdvice.measuresOf(c.name()).get(0);
            List<Figures> cards = new ArrayList<>();
            for (BuildingsTemplate t : all) if (on.serves(t)) cards.add(of(game, t, on));
            out.add(new Group(on.label(), cards, new Note(NoteKind.NONE, 0, 0, null)));
            return out;
        }
        Map<String, List<Figures>> by = new LinkedHashMap<>();
        for (String name : GROUP_ORDER) by.put(name, new ArrayList<>());
        for (BuildingsTemplate t : all) by.computeIfAbsent(group(t), k -> new ArrayList<>()).add(of(game, t, null));
        for (Map.Entry<String, List<Figures>> e : by.entrySet()) {
            if (e.getValue().isEmpty()) continue;
            out.add(new Group(e.getKey(), e.getValue(), note(game, e.getValue())));
        }
        return out;
    }

    /** The note under a market group's heading (see NoteKind). */
    static Note note(Game game, List<Figures> cards) {
        Figures f = cards.get(0);
        Sectors sectors = game.getSectors();
        switch (f.kind()) {
            case HOMES:
                return new Note(NoteKind.DOORS, sectors.realEstate().doorShortfall(false),
                        sectors.realEstate().doorShortfall(true), null);
            case CUSTOMERS:
                if (f.template().makes(Good.LUXURY_TRADE) > 0) {
                    return new Note(NoteKind.LUXURY, counted(game, sectors.luxuryRetail(), sectors.luxuryRetail().getWanted()),
                            sectors.luxuryRetail().coverage(), null);
                }
                return new Note(NoteKind.SHOPS, sectors.retail().getStoreCoverage(),
                        sectors.retail().getPopulation(), null);
            case MEALS:
                return new Note(NoteKind.MEALS, counted(game, sectors.restaurants(), sectors.restaurants().getWanted()),
                        sectors.restaurants().seats(), null);
            case RAIL:
                return new Note(NoteKind.RAIL, sectors.rail().getTradeTonnes(), sectors.rail().getCapacityTonnes(), null);
            case PUMP:
                // The forecourts' month (0.7.83): the drivers' litres, at the pump and past it, and what the stations could sell.
                return new Note(NoteKind.PUMP, sectors.retail().getPumpLitres() + sectors.retail().getQueueLitres(),
                        sectors.retail().getPumpCapacity(), Good.PETROL);
            case MAKER: {
                Sector owner = sectors.ownerOf(f.template());
                // The mine's ore and nothing else: it has its deposit on its own card.
                if (owner == null || f.template().getCategory() == BuildingType.MINING) break;
                Good g = owner.planningGood();
                if (g == null || !g.traded()) break;
                return new Note(NoteKind.MADE, game.getMarkets().get(g).getDemand(), owner.getCapacity(g), g);
            }
            default:
                break;
        }
        return new Note(NoteKind.NONE, 0, 0, null);
    }

    /**
     * The note of the group a sector's own buildings are compared under on
     * Build (0.7.30): the Sectors screen's flow reads a seller-priced good's
     * capacity off it - the shops' coverage, the counters', the kitchens'
     * seats, the railway's tonnes - so the two screens say the same. The
     * group is its first own building's (Retail's groceries, not the bank's
     * branch); NONE with no building of its own.
     */
    public static Note noteOf(Game game, Sector sector) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplatesBySector(sector.key())) {
            if (!t.isOwnedBySector() || citys(t)) continue;
            return note(game, List.of(of(game, t, null)));
        }
        return new Note(NoteKind.NONE, 0, 0, null);
    }

    /** A month's flow the save does not carry, or NaN while no month has run since the load: the sector has no word for the month. */
    static double counted(Game game, Sector sector, double flow) {
        return game.getLastInvestment(sector.key()).isEmpty() ? Double.NaN : flow;
    }

    /* =====================================================================
       THE INVESTORS' LINE (market cards)

       If investors have an order on this building's site, it says so, with
       the count and the wait - that outranks their word, which by then is
       often "already building". Otherwise it quotes the sector's own word
       for the month (Game.getLastInvestment()), which is filed per sector
       and can name another building - so it adds "this one:" and the first
       gate THIS building fails now, unless the word already names it: the
       deposit, the licence, the owner's staffing test (Sector.Staffing.why(),
       the Investors page's own words), the land, or a loss in the investors'
       estimate. The order is buildStack()'s and the planners': ore, licence,
       staff, ground, money.
       ===================================================================== */

    /** Where Game files a building's word: the branch under "Bank" (Game.consider()'s label), every other under its sector. */
    public static String slot(BuildingsTemplate t) {
        // ...and a filling station's under the forecourts' own (0.7.83; Game.STATIONS_SLOT), as the branch's is the bank's.
        if (ham.citybuildersim.sectors.Retail.isStation(t)) return Game.STATIONS_SLOT;
        return !t.isOwnedBySector() && t.getCategory() == BuildingType.COMMERCIAL ? "Bank" : t.getSector();
    }

    /** The first gate a building fails for investors now. */
    public enum GateKind { DEPOSIT, LICENCE, STAFFING, LAND, LOSS }

    /** The resource a deposit refusal names (0.7.62): "iron" for an Iron Mine (and for none), "oil" for an Oil Well. */
    public static String depositWord(Resource r) {
        return r == null || r == Resource.IRON ? "iron" : r.label().toLowerCase(java.util.Locale.ROOT);
    }

    /**
     * One gate: DEPOSIT {deposits the city owns, mines committed} with the
     * resource's word in `why` (depositWord()); LICENCE
     * {licences one needs, spare licences} with the licence; STAFFING with
     * the staffing test's own why; LAND {sq ft one needs, sq ft free}; LOSS
     * {what the estimate says it would lose a month}.
     */
    public record Gate(GateKind kind, double a, double b, JobType licence, String why) { }

    /**
     * The investors' line's figures.
     *
     * @param theirs    an order on this building's site paid by investors, with value left on it
     * @param yoursToo  ...and the city's order on it as well
     * @param onSite    what is on site of it, for anyone's order
     * @param months    the wait at today's queue (Game.onSiteMonths())
     * @param word      the sector's word for the month, unshortened; "" until a month has run since
     *                  the city was founded or loaded (it is not saved)
     * @param own       the first gate this building fails now, or null
     * @param estimate  the investors' estimate of what one would make its owner a month
     *                  (BusinessInvestment.estimatedMonthlyProfit())
     */
    public record Investors(String building, String slot, Sector owner, boolean theirs, boolean yoursToo,
                            int onSite, double months, String word, Gate own, double estimate) {

        /** "this one:" is said when there is a gate, no investor is already building it, and the word does not already name it. */
        public boolean showOwn() { return own != null && !theirs && !word.contains(building); }
    }

    public static Investors investors(Game game, BuildingsTemplate t) {
        String slot = slot(t);
        Sector owner = ownerOf(game, t);
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        int onSite = stack == null ? 0 : stack.getUnderConstruction();
        boolean theirs = false, yours = false;
        if (onSite > 0) {
            for (BuildingsStacks.Contract c : stack.getContracts()) {
                if (!(c.getValue() > 0)) continue;
                if ("City".equals(c.payer)) yours = true; else theirs = true;
            }
        }
        String word = game.getLastInvestment(slot);
        double estimate = game.getBusinessInvestment().estimatedMonthlyProfit(owner.key(), t);
        return new Investors(t.getName(), slot, owner, theirs, theirs && yours, onSite,
                onSite > 0 ? game.onSiteMonths(t) : 0, word == null ? "" : word, gate(game, t, owner, estimate), estimate);
    }

    /** The first gate investors would stop this building at now, in buildStack()'s order and then the planners': null when it passes them all. */
    static Gate gate(Game game, BuildingsTemplate t, Sector owner, double estimate) {
        LandManager ground = game.getLandManager();
        if (!game.hasDepositFor(t, 1)) {
            // ...on its own resource's sites (0.7.62), named in `why`: "iron" or "oil" - a land well's dry ones (0.7.84).
            Resource site = Game.siteOf(t);
            return new Gate(GateKind.DEPOSIT, game.sitesFor(t), game.committedFor(t), null, depositWord(site));
        }
        if (!game.hasLicencesFor(t, 1)) {
            JobType licence = t.getRequiresLicence();
            return new Gate(GateKind.LICENCE, game.licencesNeededFor(t, 1),
                    game.getPopulationManager().spareLicences(licence), licence, null);
        }
        Sector.Staffing staffing = owner.staffing(t);
        if (!staffing.passes()) return new Gate(GateKind.STAFFING, staffing.share, 0, null, staffing.why(t.getName()));
        double free = ground.getAvailableSqFt();
        if (t.getLandSqFt() > free) return new Gate(GateKind.LAND, t.getLandSqFt(), free, null, null);
        if (estimate <= 0) return new Gate(GateKind.LOSS, -estimate, 0, null, null);
        return null;
    }

    /* =====================================================================
       EVERY GATE (0.7.75, the sector statements' R4)

       gate() stops at the first gate a building fails, which is the build
       card's answer and the Investors summary's - and it can hide a second
       refusal behind the first (the project's spec-sector-statements.md,
       F11): the design study's Fabrication Shop stopped at land, and its
       estimate was a loss, so the ground the player was sent to buy would
       not have built it either. The investor report asks every gate, each
       passed, failed or not one this building has (no ore to find, no
       licence, no posts, no ground), in gate()'s order - so its first
       failure is gate()'s, which BuildCardCheck's and SectorStatementCheck's
       readers can hold it to - and then what one would earn, cost, take to
       pay back, and how it would be paid for (D8). Pure, as gate() is.
       ===================================================================== */

    /** A gate's answer for one building: it passes, it fails, or it is not a gate this building has. */
    public enum Mark { PASS, FAIL, NONE }

    /** One gate's answer: its kind (LOSS is "it pays"), the mark, and gate()'s figures when it fails. */
    public record GateMark(GateKind kind, Mark mark, Gate failure) { }

    /**
     * One building at every gate (R4).
     *
     * @param gates    ore, licence, staff, land and pays, in gate()'s order
     * @param estimate the investors' estimate of what one would make its owner a month
     * @param cost     what one would cost to build, its ground with it (BusinessInvestment.getCostOf())
     * @param payback  the months its estimate would take to earn its cost; NaN when it would not pay
     * @param owners   of the cost, what its register would ask its owners for (Game.financingOf())
     * @param own      ...what its till and its money abroad would put in
     * @param borrowed ...and what it would borrow
     */
    public record Appraisal(BuildingsTemplate template, Sector owner, List<GateMark> gates, double estimate,
                            double cost, double payback, double owners, double own, double borrowed) {

        /** The first gate it fails - gate()'s answer - or null when it passes them all. */
        public Gate first() {
            for (GateMark g : gates) if (g.mark() == Mark.FAIL) return g.failure();
            return null;
        }

        /** Every gate it fails, in order. */
        public List<GateMark> failures() {
            List<GateMark> out = new ArrayList<>();
            for (GateMark g : gates) if (g.mark() == Mark.FAIL) out.add(g);
            return out;
        }

        /** Its mark at one gate. */
        public Mark at(GateKind kind) {
            for (GateMark g : gates) if (g.kind() == kind) return g.mark();
            return Mark.NONE;
        }
    }

    /** Every gate investors would ask this building at now, and what one would earn, cost and be paid for with. */
    public static Appraisal appraise(Game game, BuildingsTemplate t) {
        Sector owner = ownerOf(game, t);
        double estimate = game.getBusinessInvestment().estimatedMonthlyProfit(owner.key(), t);
        LandManager ground = game.getLandManager();
        List<GateMark> gates = new ArrayList<>();
        // The ore: a mine's own resource's sites.
        Resource site = Game.siteOf(t);
        gates.add(site == null ? new GateMark(GateKind.DEPOSIT, Mark.NONE, null)
                : game.hasDepositFor(t, 1) ? new GateMark(GateKind.DEPOSIT, Mark.PASS, null)
                : new GateMark(GateKind.DEPOSIT, Mark.FAIL,
                        new Gate(GateKind.DEPOSIT, game.sitesFor(t), game.committedFor(t), null, depositWord(site))));
        // The licence its posts need.
        JobType licence = t.getRequiresLicence();
        gates.add(licence == null ? new GateMark(GateKind.LICENCE, Mark.NONE, null)
                : game.hasLicencesFor(t, 1) ? new GateMark(GateKind.LICENCE, Mark.PASS, null)
                : new GateMark(GateKind.LICENCE, Mark.FAIL, new Gate(GateKind.LICENCE, game.licencesNeededFor(t, 1),
                        game.getPopulationManager().spareLicences(licence), licence, null)));
        // Its owner's staffing test, for a building with posts.
        double posts = 0;
        for (JobType job : JobType.values()) posts += t.getJobs(job);
        Sector.Staffing staffing = owner.staffing(t);
        gates.add(!(posts > 0) ? new GateMark(GateKind.STAFFING, Mark.NONE, null)
                : staffing.passes() ? new GateMark(GateKind.STAFFING, Mark.PASS, null)
                : new GateMark(GateKind.STAFFING, Mark.FAIL,
                        new Gate(GateKind.STAFFING, staffing.share, 0, null, staffing.why(t.getName()))));
        // The ground.
        double free = ground.getAvailableSqFt();
        gates.add(!(t.getLandSqFt() > 0) ? new GateMark(GateKind.LAND, Mark.NONE, null)
                : t.getLandSqFt() <= free ? new GateMark(GateKind.LAND, Mark.PASS, null)
                : new GateMark(GateKind.LAND, Mark.FAIL, new Gate(GateKind.LAND, t.getLandSqFt(), free, null, null)));
        // ...and whether it pays.
        gates.add(estimate > 0 ? new GateMark(GateKind.LOSS, Mark.PASS, null)
                : new GateMark(GateKind.LOSS, Mark.FAIL, new Gate(GateKind.LOSS, -estimate, 0, null, null)));
        double[] f = game.financingOf(owner.key(), t);
        return new Appraisal(t, owner, gates, estimate, f[0], estimate > 0 ? f[0] / estimate : Double.NaN, f[1], f[2], f[3]);
    }

    /** Every market building one sector owns, appraised at every gate, in the catalogue's order: the investor report's (R4). */
    public static List<Appraisal> appraiseAll(Game game, Sector sector) {
        List<Appraisal> out = new ArrayList<>();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (citys(t) || ownerOf(game, t) != sector) continue;
            out.add(appraise(game, t));
        }
        return out;
    }

    /* =====================================================================
       THE WORD'S KIND (0.7.30)

       The Sectors screen draws each sector's word for the month with an
       icon and a colour - building, selling, waiting on ground, on staff, on
       a licence, ore, supply, credit or money, or nothing more wanted - and
       says the kind in a word before it. The kind is read off the word's own
       text, as LongPlaytest.houseReason() reads the landlords': the words
       are filed at about forty-five sites in fifteen files, each a sentence,
       and a kind carried on every BusinessInvestment.Decision is the cleaner
       change and the much larger one (the project's spec-sectors-0730.md,
       D2). The rules are tried in WordKind's order and the first that
       matches wins - so a "Declined" that names the lender is CREDIT, and a
       word about staff that also names the bank is STAFF - and a phrase
       matches as whole words: "ore" is not in "more", "before" or
       "Convenience Store". BuildCardCheck holds an example of every phrase.
       ===================================================================== */

    /**
     * What a sector's word is about. BUILDING: investors have an order on
     * site, or the word says it built; SELLING: it sold buildings back;
     * LAND: no ground, or the month's land-blocked list has it; STAFF: the
     * staffing test; LICENCE: licences; ORE: a deposit or the ore; SUPPLY:
     * a part the city does not make enough of and the world will not sell
     * (the car plants' fabricated steel - not in the design study's table,
     * which had no kind for it); CREDIT:
     * the bank, a lender, a ban, the default point, a mortgage or its down
     * payment; MONEY: it does not pay; ENOUGH: there is enough already, or
     * not yet enough wanted for a first; NONE: no word yet (a month has to
     * run after a load); OTHER: none of these.
     */
    public enum WordKind { BUILDING, SELLING, LAND, STAFF, LICENCE, ORE, SUPPLY, CREDIT, MONEY, ENOUGH, NONE, OTHER }

    /** The phrases each kind is read by, in lower case; a word starting "Could not build" is LAND as well, and one starting "Declined" is MONEY unless CREDIT took it. */
    static final String[] LAND_WORDS    = { "no land", "short of homes" };
    /** ...STAFF's. */
    static final String[] STAFF_WORDS   = { "could staff" };
    /** ...LICENCE's: whole words, so the plural as well. */
    static final String[] LICENCE_WORDS = { "licence", "licences" };
    /** ...ORE's. */
    static final String[] ORE_WORDS     = { "deposit", "ore" };
    /** ...SUPPLY's. */
    static final String[] SUPPLY_WORDS  = { "fabricates", "will not sell" };
    /** ...CREDIT's. */
    static final String[] CREDIT_WORDS  = { "borrowing ban", "bank", "default point", "down payment", "lender", "mortgage" };
    /** ...MONEY's. */
    static final String[] MONEY_WORDS   = { "below cost", "no margin", "worth sinking", "worth building", "cost more than",
                                            "nothing left in it", "does not pay", "does not cover", "would not clear",
                                            "the ground under", "wage bill", "in fees against" };
    /** ...ENOUGH's. */
    static final String[] ENOUGH_WORDS  = { "ahead of", "already", "covers all", "months of work queued",
                                            "months of work on site", "not enough for a first", "nothing that adds capacity",
                                            "none for another", "nothing crosses", "the smallest" };

    /** The kind of a word on its text alone. */
    public static WordKind wordKind(String word) { return wordKind(word, false, false); }

    /**
     * ...with what the text cannot say: whether investors have an order on
     * site of one of the sector's buildings (`building`), and whether the
     * month put it on Game.getLandBlockedSectors() (`landBlocked`).
     */
    public static WordKind wordKind(String word, boolean building, boolean landBlocked) {
        String w = word == null ? "" : word;
        String lower = w.toLowerCase(java.util.Locale.ROOT);
        if (building || w.startsWith("Built")) return WordKind.BUILDING;
        if (w.startsWith("Sold")) return WordKind.SELLING;
        if (landBlocked || w.startsWith("Could not build") || says(lower, LAND_WORDS)) return WordKind.LAND;
        if (says(lower, STAFF_WORDS)) return WordKind.STAFF;
        if (says(lower, LICENCE_WORDS)) return WordKind.LICENCE;
        if (says(lower, ORE_WORDS)) return WordKind.ORE;
        if (says(lower, SUPPLY_WORDS)) return WordKind.SUPPLY;
        if (says(lower, CREDIT_WORDS)) return WordKind.CREDIT;
        if (w.startsWith("Declined") || says(lower, MONEY_WORDS)) return WordKind.MONEY;
        if (says(lower, ENOUGH_WORDS)) return WordKind.ENOUGH;
        return w.isEmpty() ? WordKind.NONE : WordKind.OTHER;
    }

    /** Whether any of the phrases is in the text as whole words: no letter or digit either side of it. */
    static boolean says(String text, String[] phrases) {
        for (String p : phrases) {
            for (int at = text.indexOf(p); at >= 0; at = text.indexOf(p, at + 1)) {
                int end = at + p.length();
                boolean before = at == 0 || !Character.isLetterOrDigit(text.charAt(at - 1));
                boolean after = end >= text.length() || !Character.isLetterOrDigit(text.charAt(end));
                if (before && after) return true;
            }
        }
        return false;
    }

    /* =====================================================================
       ONE SECTOR'S INVESTORS (0.7.30)

       The Sectors screen's line under every page's strip and on every card
       of the list, and its Investors page: the sector's word for the month,
       its kind, and each of the market buildings it owns with its own line
       (investors() above) - so the two screens word one record the same way
       (the project's spec-sectors-0730.md, D5).
       ===================================================================== */

    /**
     * One sector's investors.
     *
     * @param word        the word it filed for the month (Game.getLastInvestment()); "" until a month has run
     * @param kind        the word's kind, investors on site and the land-blocked list counted (wordKind())
     * @param buildings   each market building it owns, with its own line, in the catalogue's order - Retail's
     *                    bank branch among them (ownerOf())
     * @param theirs      investors have an order with value on the site of one of them
     * @param yoursToo    ...and the city has an order on one of those sites too
     * @param onSite      what is on site of the buildings investors are putting up, for anyone's order
     * @param months      the longest wait at today's queue among those sites; 0 with none
     * @param landBlocked the month put it on Game.getLandBlockedSectors()
     */
    public record SectorInvestors(Sector owner, String word, WordKind kind, List<Investors> buildings,
                                  boolean theirs, boolean yoursToo, int onSite, double months, boolean landBlocked) {

        /** The sector's line as a build card's line is worded: its own name, its word, no gate of its own. */
        public Investors line() {
            return new Investors(owner.label(), owner.key(), owner, theirs, yoursToo, onSite, months, word, null, Double.NaN);
        }
    }

    public static SectorInvestors sectorInvestors(Game game, Sector sector) {
        List<Investors> all = new ArrayList<>();
        boolean theirs = false, yours = false;
        int onSite = 0;
        double months = 0;
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (citys(t) || ownerOf(game, t) != sector) continue;
            Investors i = investors(game, t);
            all.add(i);
            if (!i.theirs()) continue;
            theirs = true;
            yours |= i.yoursToo();
            onSite += i.onSite();
            months = Math.max(months, i.months());
        }
        String word = game.getLastInvestment(sector.key());
        if (word == null) word = "";
        boolean blocked = game.getLandBlockedSectors().contains(sector.key());
        return new SectorInvestors(sector, word, wordKind(word, theirs, blocked), all,
                theirs, yours, onSite, months, blocked);
    }

    /* =====================================================================
       THE VERDICT ON AN ORDER

       What the quote line says under the stepper, in buildStack()'s order:
       no deposit, no coast (0.7.59), nobody licensed, short of land, short of cash (the bill
       and credit offers), or the wait at today's queue - so the two hard
       refusals are warned before the click, not discovered after it.
       ===================================================================== */

    public enum VerdictKind { NO_DEPOSIT, NO_LICENCE, NO_LAND, BILL, MONTHS, NO_COAST }

    /** The verdict, the quote it is on, and how short: sq ft of land, cash, or the months to wait - and, refused for a deposit, whose sites it needs (0.7.62; null otherwise). */
    public record Verdict(VerdictKind kind, Game.BuildQuote quote, double figure, Resource site) {
        public Verdict(VerdictKind kind, Game.BuildQuote quote, double figure) { this(kind, quote, figure, null); }
    }

    public static Verdict verdict(Game game, BuildingsTemplate t, int n) {
        Game.BuildQuote q = game.quoteBuild(t, n);
        if (!game.hasDepositFor(t, n)) return new Verdict(VerdictKind.NO_DEPOSIT, q, 0, Game.siteOf(t));
        if (!game.hasCoastFor(t, n)) return new Verdict(VerdictKind.NO_COAST, q, 0);
        if (!game.hasLicencesFor(t, n)) return new Verdict(VerdictKind.NO_LICENCE, q, game.licencesNeededFor(t, n));
        if (q.landNeeded > q.landFree) return new Verdict(VerdictKind.NO_LAND, q, q.landNeeded - q.landFree);
        if (q.total > game.getCash()) return new Verdict(VerdictKind.BILL, q, q.total - game.getCash());
        return new Verdict(VerdictKind.MONTHS, q, q.months);
    }
}
