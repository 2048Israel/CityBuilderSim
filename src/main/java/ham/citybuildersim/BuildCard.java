package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One build card's figures, for every one of the buildings in buildings.json
 * (0.7.25; 142 of them since FR7, the Rail Depot): what
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
 * Since 0.7.142 (batch I13) a materials building's card says where it
 * stands, how its sector weighs it, its own power, the old metal it buys,
 * who here buys what it makes and a crew's E (THE MATERIALS' CARDS), and
 * the nine industries' pages end on the same chain (chainLines()).
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
     * pipeline. DEPOT (FR2): the trucks a Truck Depot keeps.
     */
    public enum Kind { CITY, HOMES, CUSTOMERS, MEALS, BRANCH, RAIL, POINTS, MAKER, OFFICE, PUMP, TANKS, BERTHS, SLOTS, PIPE, DEPOT }

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
     * @param landFree   the land free, for the needs line's share of it: since 0.7.109 the ground its sector may
     *                   build on (LandManager.freeFor(); getAvailableSqFt() until then, the same with no zone)
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
       has fourteen groups by owning sector since 0.7.138 (and four by kind
       beside them: oil storage, the ports and the oil at sea's platforms and
       pipelines) and Shops three (the grocers', their forecourts and the
       bank's branches), and the bars and tags compare within a
       group: a steel mill against a steel mill, not against a bakery. The
       names are the design note's, and since 0.7.62 the wells' and the
       refinery's ("Oil", "Refining") after the mines'; since 0.7.120 (batch
       I3, Jerus's Q2) the mills' and the mines' are their sectors' new
       labels, "Iron & Steel" and "Iron Mining" ("Steel" and "Iron" until
       then); since 0.7.126 (batch I6) Coal Mining's "Coal" after the iron
       mines', the materials chains' groups by the spec's 4 ("Coal, Copper,
       Stone, Wood, Cement"); since 0.7.131 (batch I7) Quarrying's "Stone"
       and Cement & Concrete's "Cement" after it; since 0.7.133 (batch I8)
       "Copper" after them, Copper Mining's and Copper Refining's together
       (★ the spec's one word for the chain, as its list gives it); since
       0.7.138 (batch I9) Forestry's "Wood" after copper, its crews and its
       planted stands together (the spec's 4: "Wood" for Forestry), and since
       0.7.140 (batch I10) Wood Products' mills in it beside them (★ the
       chain's one word, as copper's two sectors share theirs).
       ===================================================================== */

    /** The market groups, in the order a page lays them out. */
    static final String[] GROUP_ORDER = {
            "Homes", "Groceries", "Filling stations", "The bank's branches",
            "Food mills", "Food processing", "Iron & Steel", "Fabrication & machinery", "Iron Mining", "Coal", "Stone", "Cement", "Copper", "Wood", "Oil",
            "Oil platforms",
            "Oil pipelines", "Refining",
            "Oil storage", "Ports", "Building materials", "Builders",
            "Offices", "Farms", "Rail", "Truck depots", "Vehicles", "Luxury shops", "Restaurants" };

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
        // ...and Freight's Truck Depot, measured in the trucks it keeps, apart from the railway's tonnes (FR2).
        if (kindOf(t) == Kind.DEPOT) return "Truck depots";
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
                    case Sectors.HEAVY_INDUSTRY:  return "Iron & Steel";
                    case Sectors.MANUFACTURING:   return "Fabrication & machinery";
                    case Sectors.MINING:          return "Iron Mining";
                    case Sectors.COAL_MINING:     return "Coal";
                    case Sectors.QUARRYING:       return "Stone";
                    case Sectors.CEMENT_CONCRETE: return "Cement";
                    case Sectors.COPPER_MINING:   return "Copper";
                    case Sectors.COPPER_REFINING: return "Copper";
                    case Sectors.FORESTRY:        return "Wood";
                    case Sectors.WOOD_PRODUCTS:   return "Wood";
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
        // ...and Freight's Truck Depot (FR2), measured in the trucks it keeps.
        if (t.getTruckRoom() > 0) return Kind.DEPOT;
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
     * with no unit (Refining.feedValueOf()), its spread on the whole feed. A
     * coal preparation plant (0.7.126) neither: what it adds to Coal Mining's
     * flow (sectors.CoalMining.madeBy()).
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
        // ...and a coal preparation plant (0.7.126) uses no good either: what it adds to Coal Mining's flow, the coking coal
        // it makes less the thermal its raw coal no longer sells as (sectors.CoalMining.madeBy()) - nothing with no raw coal.
        if (t.getWash() > 0 && game.getSectors() != null) {
            for (Map.Entry<Good, Double> e : game.getSectors().coalMining().madeBy(t).entrySet()) {
                double p = priceOf(game, e.getKey());
                if (Double.isFinite(p)) v += e.getValue() * p;
            }
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
        // ...on the ground its sector may build on (0.7.109, Z2): free for anyone, and its zone's (LandManager.freeFor()).
        double free = game.getLandManager().freeFor(t.getSector());
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
            case DEPOT:
                verb = "keeps up to "; words = " trucks"; figure = unit = t.getTruckRoom();
                per = perTag = "truck kept";
                break;
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
                // ...and a coal preparation plant (0.7.126) by the raw coal it washes: a stream of the coking mines', no good.
                if (t.getWash() > 0) {
                    verb = "washes "; words = " t of raw coal a month";
                    figure = t.getWash();
                }
                va = valueAdded(game, t); unit = va;
                Resource site = Game.siteOf(t);
                if (site != null) {
                    // ...the sites of its own resource (0.7.62): an Oil Well's oil, a mine's iron - a land well's dry ones (0.7.84,
                    // Game.sitesFor()), a platform's well its platforms' slots (0.7.91), counted against its own kind (committedFor()),
                    // and the tonnes of its own pool (0.7.93, Game.remainingFor()): a land well's the ground's, a platform's the sea's.
                    detail = new double[] { game.sitesFor(t), game.committedFor(t), game.remainingFor(t) };
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
     * was loaded (Game.hasMonthRun()) they read zero,
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
                return new Note(NoteKind.RAIL, sectors.freight().getTradeTonnes(), sectors.freight().getCapacityTonnes(), null);
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

    /** A month's flow the save does not carry, or NaN while no month has run since the load (Game.hasMonthRun(); until 0.7.102, while the sector had no word for the month - the word is saved since). */
    static double counted(Game game, Sector sector, double flow) {
        return game.hasMonthRun() ? flow : Double.NaN;
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
       the Investors page's own words), the land, a loss in the investors'
       estimate, or since 0.7.122 the interest on its whole cost. The order
       is buildStack()'s and the planners': ore, licence, staff, ground,
       money - and then consider()'s brake, which every order is asked last.

       THE INTEREST ON ITS WHOLE COST (0.7.122, batch C1). The gates ended at
       the loss, "it pays": an estimate above nothing. Game.consider() asks
       more of every order, since 0.7.113 (batch I2b, Jerus's decisions A and
       A13) on its whole cost whoever pays: that it earn PROFIT_OVER_INTEREST
       times a month's interest on all it costs at the rate it would be
       judged at. So a card could pass every gate while the investors
       declined the building - the docs pass for 0.7.115 found it: in
       BuildCardCheck's town an Industrial Bakery earning $97.6k a month
       showed no gate, where the bar was $154.7k (1.25 times $123.8k of
       interest a month on its $29.5M at 5.03%). The card asks it now, as its
       last gate (INTEREST), through the model's own door
       (Game.interestTest(), carriesItsWholeCost()'s answer with its
       figures), and its words say what one would earn, its interest and the
       rate. A landlord's home is not asked it: the mortgage lender's test
       decides it (Game.buysOnMortgage(), THE LANDLORDS' MORTGAGES).
       ===================================================================== */

    /** Where Game files a building's word: the branch under "Bank" (Game.consider()'s label), every other under its sector. */
    public static String slot(BuildingsTemplate t) {
        // ...and a filling station's under the forecourts' own (0.7.83; Game.STATIONS_SLOT), as the branch's is the bank's.
        if (ham.citybuildersim.sectors.Retail.isStation(t)) return Game.STATIONS_SLOT;
        return !t.isOwnedBySector() && t.getCategory() == BuildingType.COMMERCIAL ? "Bank" : t.getSector();
    }

    /** The first gate a building fails for investors now: the last, INTEREST, is consider()'s brake on its whole cost (0.7.122). */
    public enum GateKind { DEPOSIT, LICENCE, STAFFING, LAND, LOSS, INTEREST }

    /** The resource a deposit refusal names (0.7.62): "iron" for an Iron Mine (and for none), "oil" for an Oil Well. */
    public static String depositWord(Resource r) {
        return r == null || r == Resource.IRON ? "iron" : r.label().toLowerCase(java.util.Locale.ROOT);
    }

    /*
     * THE TWO CARD WORDS (0.7.103, batch P4; Jerus's B, 2026-10-10). A land
     * Oil Well counts the city's DRY oil sites only (Game.sitesFor(), 0.7.84 -
     * the sea's are a platform's), where the land office counts every site,
     * so its card says "dry" on the count. And a refinery's conversion unit
     * with no crude unit standing has no stream to take, so its estimate is
     * its running and standing costs and nothing else: its card says NO_FEED
     * in place of the loss.
     */

    /** A DEPOSIT gate's word for a land Oil Well (0.7.103): its count is of dry sites (Game.sitesFor()). */
    public static final String DRY_OIL = "dry oil";

    /** What a conversion unit's card says in place of a loss with no crude unit standing to feed it (0.7.103). */
    public static final String NO_FEED = "no feed: no crude unit stands to run its stream";

    /** The word a building's site count carries (0.7.103): " dry" for a land Oil Well, whose count is the dry sites alone; "" for any other. */
    public static String siteCountWord(BuildingsTemplate t) {
        return ham.citybuildersim.sectors.Oil.isLandWell(t) ? " dry" : "";
    }

    /** A DEPOSIT gate's word: depositWord(), or DRY_OIL for a land Oil Well (0.7.103). */
    static String depositWordFor(BuildingsTemplate t, Resource r) {
        return ham.citybuildersim.sectors.Oil.isLandWell(t) ? DRY_OIL : depositWord(r);
    }

    /** Whether a building is a refinery's conversion unit with no crude unit standing to feed it (0.7.103): its estimate is a loss only for want of a stream. */
    public static boolean unfed(Game game, BuildingsTemplate t) {
        if (!ham.citybuildersim.sectors.Refining.isConversionUnit(t)) return false;
        BuildingManager b = game.getBuildingManager();
        return !(b.totalBySector(Sectors.REFINING, c -> ham.citybuildersim.sectors.Refining.isCrudeUnit(c) ? 1 : 0) > 0);
    }

    /**
     * The LAND gate (0.7.109, batch Z2): the ground one needs against the
     * ground its sector may build on (LandManager.freeFor(): free for anyone,
     * and its zone's), and in `why` what the other sectors' zones hold
     * (BusinessInvestment.zonedForOthersWords(), the investors' own words:
     * "55.9 km² is zoned for others") - null with no other zone holding any.
     */
    static Gate landGate(LandManager ground, BuildingsTemplate t, double free) {
        double others = ground.heldByZones() - ground.heldBy(t.getSector());
        return new Gate(GateKind.LAND, t.getLandSqFt(), free, null,
                others > 0 ? BusinessInvestment.zonedForOthersWords(others) : null);
    }

    /** The LOSS gate: what it would lose, worded NO_FEED for a conversion unit nothing feeds (0.7.103). */
    static Gate lossGate(Game game, BuildingsTemplate t, double estimate) {
        return new Gate(GateKind.LOSS, -estimate, 0, null, unfed(game, t) ? NO_FEED : null);
    }

    /** Whether investors ask a building the INTEREST gate (0.7.122): every order but a landlord's home, which the mortgage lender's test decides. */
    static boolean asksInterest(Game game, BuildingsTemplate t, Sector owner) {
        return !game.buysOnMortgage(owner.key(), t);
    }

    /**
     * The INTEREST gate (0.7.122, batch C1): consider()'s test of an order of
     * one, asked through the model's door - Game.interestTest(), whose answer
     * is carriesItsWholeCost()'s - with its figures {what one would earn a
     * month, what it must: PROFIT_OVER_INTEREST times a month's interest on
     * its whole cost} and the test itself (the interest, the cost it is
     * struck on, the rate); null where it carries it or is not asked it
     * (asksInterest()). Under a borrowing ban the test says no to a building
     * its till cannot pay for, and `why` says so in consider()'s words
     * (banWords()).
     */
    static Gate interestGate(Game game, BuildingsTemplate t, Sector owner, double estimate) {
        if (!asksInterest(game, t, owner)) return null;
        Game.InterestTest test = game.interestTest(owner.key(), estimate, game.getBusinessInvestment().getCostOf(t, 1));
        if (test.carries()) return null;
        return new Gate(GateKind.INTEREST, estimate, test.needs(), null, test.banned() ? banWords(game, owner) : null, test);
    }

    /** What an INTEREST gate says under a borrowing ban, in consider()'s words: "borrowing ban, 7 more months - it would need credit". */
    static String banWords(Game game, Sector owner) {
        return String.format("borrowing ban, %d more months - it would need credit",
                game.getEconomyManager().getBusinessDebtManager().getBlockedMonths(owner.key()));
    }

    /**
     * One gate: DEPOSIT {deposits the city owns, mines committed} with the
     * resource's word in `why` (depositWord(); DRY_OIL for a land Oil Well
     * since 0.7.103); LICENCE
     * {licences one needs, spare licences} with the licence; STAFFING with
     * the staffing test's own why; LAND {sq ft one needs, sq ft free}, with
     * what the other sectors' zones hold in `why` (0.7.109; landGate()); LOSS
     * {what the estimate says it would lose a month}, with NO_FEED in `why`
     * for a conversion unit nothing feeds (0.7.103); INTEREST {what one would
     * earn a month, what it must} with the model's test in `test` - its
     * interest, the cost it is struck on, the rate - and the ban's words in
     * `why` under a borrowing ban (0.7.122; interestGate()).
     */
    public record Gate(GateKind kind, double a, double b, JobType licence, String why, Game.InterestTest test) {

        /** Every gate but INTEREST: no test of its own. */
        public Gate(GateKind kind, double a, double b, JobType licence, String why) { this(kind, a, b, licence, why, null); }
    }

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
     * @param planned   a materials building weighed by the rule its sector orders by (0.7.142,
     *                  weighed()), or null for any other
     */
    public record Investors(String building, String slot, Sector owner, boolean theirs, boolean yoursToo,
                            int onSite, double months, String word, Gate own, double estimate, Weighed planned) {

        /** "this one:" is said when there is a gate, no investor is already building it, and the word does not already name it. */
        public boolean showOwn() { return own != null && !theirs && !word.contains(building); }

        /**
         * ...and for a materials building the planner's gate is said in its
         * place (0.7.142): the first its sector asks, in SpreadPlanner's words,
         * on the same terms - when it fails one, no investor is building it and
         * the word does not already name it.
         */
        public boolean showPlanned() { return planned != null && !planned.passes() && !theirs && !word.contains(building); }
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
                onSite > 0 ? game.onSiteMonths(t) : 0, word == null ? "" : word, gate(game, t, owner, estimate), estimate,
                weighed(game, t));
    }

    /** The first gate investors would stop this building at now, in buildStack()'s order and then the planners': null when it passes them all. */
    static Gate gate(Game game, BuildingsTemplate t, Sector owner, double estimate) {
        LandManager ground = game.getLandManager();
        if (!game.hasDepositFor(t, 1)) {
            // ...on its own resource's sites (0.7.62), named in `why`: "iron" or "oil" - a land well's dry ones (0.7.84).
            Resource site = Game.siteOf(t);
            return new Gate(GateKind.DEPOSIT, game.sitesFor(t), game.committedFor(t), null, depositWordFor(t, site));
        }
        if (!game.hasLicencesFor(t, 1)) {
            JobType licence = t.getRequiresLicence();
            return new Gate(GateKind.LICENCE, game.licencesNeededFor(t, 1),
                    game.getPopulationManager().spareLicences(licence), licence, null);
        }
        Sector.Staffing staffing = owner.staffing(t);
        if (!staffing.passes()) return new Gate(GateKind.STAFFING, staffing.share, 0, null, staffing.why(t.getName()));
        double free = ground.freeFor(t.getSector());
        if (t.getLandSqFt() > free) return landGate(ground, t, free);
        if (estimate <= 0) return lossGate(game, t, estimate);
        // ...and consider()'s brake, the interest on its whole cost (0.7.122).
        return interestGate(game, t, owner, estimate);
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
       Since 0.7.122 the last gate is the interest on its whole cost
       (INTEREST), asked of a building that loses money too: the model's
       test fails it as well, and the screen says the loss.
       ===================================================================== */

    /** A gate's answer for one building: it passes, it fails, or it is not a gate this building has. */
    public enum Mark { PASS, FAIL, NONE }

    /** One gate's answer: its kind (LOSS is "it pays", INTEREST "it carries its interest on its whole cost"), the mark, and gate()'s figures when it fails. */
    public record GateMark(GateKind kind, Mark mark, Gate failure) { }

    /**
     * One building at every gate (R4).
     *
     * @param gates    ore, licence, staff, land, pays and interest, in gate()'s order
     * @param estimate the investors' estimate of what one would make its owner a month
     * @param cost     what one would cost to build, its ground with it (BusinessInvestment.getCostOf())
     * @param payback  the months its estimate would take to earn its cost; NaN when it would not pay
     * @param owners   of the cost, what its register would ask its owners for (Game.financingOf())
     * @param own      ...what its till and its money abroad would put in
     * @param borrowed ...and what it would borrow
     * @param planned  a materials building weighed by the rule its sector orders by - its whole cost with
     *                 its lorries, its earnings and the planner's first gate (0.7.142, weighed()) - or null
     */
    public record Appraisal(BuildingsTemplate template, Sector owner, List<GateMark> gates, double estimate,
                            double cost, double payback, double owners, double own, double borrowed, Weighed planned) {

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
                        new Gate(GateKind.DEPOSIT, game.sitesFor(t), game.committedFor(t), null, depositWordFor(t, site))));
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
        double free = ground.freeFor(t.getSector());
        gates.add(!(t.getLandSqFt() > 0) ? new GateMark(GateKind.LAND, Mark.NONE, null)
                : t.getLandSqFt() <= free ? new GateMark(GateKind.LAND, Mark.PASS, null)
                : new GateMark(GateKind.LAND, Mark.FAIL, landGate(ground, t, free)));
        // ...whether it pays...
        gates.add(estimate > 0 ? new GateMark(GateKind.LOSS, Mark.PASS, null)
                : new GateMark(GateKind.LOSS, Mark.FAIL, lossGate(game, t, estimate)));
        // ...and whether it carries the interest on its whole cost, consider()'s brake (0.7.122): not a landlord's home's.
        boolean asked = asksInterest(game, t, owner);
        Gate interest = asked ? interestGate(game, t, owner, estimate) : null;
        gates.add(!asked ? new GateMark(GateKind.INTEREST, Mark.NONE, null)
                : interest == null ? new GateMark(GateKind.INTEREST, Mark.PASS, null)
                : new GateMark(GateKind.INTEREST, Mark.FAIL, interest));
        double[] f = game.financingOf(owner.key(), t);
        return new Appraisal(t, owner, gates, estimate, f[0], estimate > 0 ? f[0] / estimate : Double.NaN, f[1], f[2], f[3],
                weighed(game, t));
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
       THE MATERIALS' CARDS (0.7.142, batch I13; docs/studies/spec-materials.md
       2.13, its last bullet, and 5's I13 row)

       WHY. The nine materials industries' forty-seven buildings - Iron &
       Steel's, the mines', the quarries', the kilns', copper's, the crews'
       and the mills' (ids 11, 12, 14 and 101 to 138) - came onto the 0.7.25
       card as they were added, and it read each as any maker: the hero, what
       one adds at today's prices, the investors' estimate and consider()'s
       gates. But a works is ordered by its sector's SpreadPlanner, which
       weighs it on its whole cost, its lorries in it, and asks its own gates
       first - feed, ground, staff, money; a mine only on one field of its
       type with room for all its sites; a crew only while the forest lasts
       FOREST_CREW_MONTHS of every crew's cut (E). The card said none of it:
       not where a 20-site works stands ("needs a deposit: the city owns 27"
       on 27 hematite sites over three fields, none with 20 free), nor what a
       works' own power sells, what a yard pays for the city's old metal, or
       who here buys what it makes - so the four wood products, which nothing
       here buys yet, looked like any good. Each piece below is a model read,
       named where it is made; ui/BuildScreen words them (THE MATERIALS'
       LINES) and BuildCardCheck 12 holds them.
         - WHERE IT STANDS (stands()): its resource and type, the sites one
           takes, the city's sites by type, those spoken for, what is left,
           whether one more fits on one field and the plan's words for the
           field it lacks; and what one more would make where the ground puts
           it - a quarry on limestone makes limestone, not the aggregate its
           row says.
         - WEIGHED (weighed()): what one more would earn on its whole cost by
           the rule its sector orders by, its lorries' share of that cost, the
           money gate's bar, and the first of the planner's gates it fails in
           the planner's own words. The investors' line says it in place of
           consider()'s gate (Investors.showPlanned()), and the investor
           report carries it (Appraisal.planned()).
         - ITS OWN POWER (power()), THE OLD METAL IT BUYS (oldMetal()), WHO
           BUYS WHAT IT MAKES (outlets(), buyers()) and THE CREW RULE
           (crewRule(), which Policy › Zoning's forest law says too).
       ===================================================================== */

    /** Whether a building is one of the nine materials industries' (0.7.142): its owner a ProcessingSector or an ExtractionSector. */
    public static boolean materials(Game game, BuildingsTemplate t) {
        Sector owner = t == null ? null : game.getSectors().ownerOf(t);
        return owner instanceof ham.citybuildersim.sectors.ProcessingSector
                || owner instanceof ham.citybuildersim.sectors.ExtractionSector;
    }

    /**
     * Where a building on a typed resource's sites stands (0.7.142): its
     * resource and the type of field it needs (null for ANY - a quarry), the
     * sites one takes, the city's sites of its type and those spoken for
     * (Game.sitesFor(), committedFor() - every type's for ANY), what is left
     * of its type in the ground (Game.remainingFor()), the city's sites of
     * each type in Resource.typesOf()'s order (LandManager.getSites(r,
     * type)), whether one more fits on one field of its type beside the
     * works standing, on site or ordered (Game.hasDepositFor()), the field it
     * lacks in the plan's words (ExtractionSector.fieldWanting(); null when
     * it fits), and what one more would make where the ground would put it:
     * the sector's own madeBy() for a quarry (limestone while a limestone
     * field has room) and a coal mine (raw coal washed or sold as thermal),
     * its row's makes for any other.
     */
    public record Stands(Resource resource, Deposit.FieldType type, int sites, int owned, int committed, double left,
                         long[] byType, boolean fits, String wanting, Map<Good, Double> makes) {

        /** Whether it stands on its resource's fields of either type (a Quarry, a Large Quarry). */
        public boolean anyType() { return type == null; }
    }

    /** ...for a building on a typed resource's sites (iron, coal, copper, stone); null for any other. */
    public static Stands stands(Game game, BuildingsTemplate t) {
        BuildingsTemplate.Site site = t == null ? null : t.site();
        if (site == null || site.resource() == null || !site.resource().hasTypes()) return null;
        Resource r = site.resource();
        LandManager land = game.getLandManager();
        List<Deposit.FieldType> types = r.typesOf();
        long[] byType = new long[types.size()];
        for (int i = 0; i < byType.length; i++) byType[i] = land.getSites(r, types.get(i));
        Sector owner = game.getSectors().ownerOf(t);
        String wanting = owner instanceof ham.citybuildersim.sectors.ExtractionSector e ? e.fieldWanting(t) : null;
        Map<Good, Double> makes = owner instanceof ham.citybuildersim.sectors.Quarrying q ? q.madeBy(t)
                : owner instanceof ham.citybuildersim.sectors.CoalMining c ? c.madeBy(t) : t.goodsMade();
        return new Stands(r, site.type(), site.sites(), game.sitesFor(t), game.committedFor(t), game.remainingFor(t),
                byType, game.hasDepositFor(t, 1), wanting, makes);
    }

    /**
     * A materials building weighed by the rule its sector orders by
     * (0.7.142): what one more would earn a month at the city's values, its
     * freight in it (FR2: its vans' wear and diesel and its bulk's city legs
     * at Haulage's quote); its whole cost, its ground and its vans with it; its
     * vans (ProcessingSector.vansFor(), its goods') and what they cost of that
     * whole (fleetCapital()); what the money gate asks it to
     * earn a month on that cost (SpreadPlanner.hurdle()); and the first gate
     * it fails in SpreadPlanner.Gate's order - FEED (a works' feed, a mine's
     * field, a crew's forest under E, a washing plant's raw coal), LAND,
     * STAFF, MONEY - with that gate's refusal in the planner's own words;
     * failed null when it passes them all.
     */
    public record Weighed(double earns, double cost, double vans, double fleet, double hurdle,
                          ham.citybuildersim.sectors.SpreadPlanner.Gate failed, String why) {

        /** What it earns a month on its whole cost; 0 with no cost (SpreadPlanner.Candidate.score()). */
        public double score() { return cost > 0 ? earns / cost : 0; }

        /** Whether it passes every gate its sector asks. */
        public boolean passes() { return failed == null; }
    }

    /** What a washing plant's feed gate says while its sector's coking mines make no raw coal its plants do not wash (CoalMining.unwashed()). */
    public static final String NOTHING_TO_WASH = "no raw coal goes unwashed";

    /**
     * ...a works by its sector's own appraisal (ProcessingSector.appraise():
     * the candidate the plan weighs, to the bit); a mine, a crew or a washing
     * plant as its sector's plan weighs one - ExtractionSector
     * .estimatedMonthlyProfit() on plans.getCostOf() and its vans
     * (fleetCapital()), asked SpreadPlanner.judge()'s gates with its feed:
     * the field it lacks (ExtractionSector.fieldWanting()), E's refusal
     * (crewRule(), eWords()), or NOTHING_TO_WASH. Null for any other
     * building, and for a planted stand, which Forestry plants on its zone
     * and does not weigh.
     */
    public static Weighed weighed(Game game, BuildingsTemplate t) {
        Sector owner = t == null ? null : game.getSectors().ownerOf(t);
        BusinessInvestment plans = game.getBusinessInvestment();
        double vans = ham.citybuildersim.sectors.ProcessingSector.vansFor(t);
        if (owner instanceof ham.citybuildersim.sectors.ProcessingSector p) {
            if (t.goodsMade().isEmpty() || game.getMarkets() == null) return null;
            ham.citybuildersim.sectors.SpreadPlanner.Candidate c = p.appraise(t, plans);
            double fleet = ham.citybuildersim.sectors.ProcessingSector.fleetCapital(t,
                    ham.citybuildersim.sectors.SpreadPlanner.values(game.getMarkets()));
            return new Weighed(c.earns(), c.cost(), vans, fleet, p.planner().hurdle(p, plans, game, c.cost()), c.failed(), c.why());
        }
        if (!(owner instanceof ham.citybuildersim.sectors.ExtractionSector e)) return null;
        String feed;
        if (owner instanceof ham.citybuildersim.sectors.Forestry) {
            if (!ham.citybuildersim.sectors.Forestry.isCrew(t)) return null;
            CrewRule rule = crewRule(game, t);
            feed = rule.room() ? null : eWords(rule, t);
        } else if (t.getWash() > 0 && owner instanceof ham.citybuildersim.sectors.CoalMining coal) {
            feed = coal.unwashed() > 0 ? null : NOTHING_TO_WASH;
        } else if (t.site() != null && t.site().resource() == e.resource()) {
            feed = e.fieldWanting(t);
        } else {
            return null;
        }
        double fleet = e.fleetCapital(t);
        double cost = plans.getCostOf(t, 1) + fleet;
        double earns = e.estimatedMonthlyProfit(t, plans);
        ham.citybuildersim.sectors.SpreadPlanner.Candidate c = ham.citybuildersim.sectors.SpreadPlanner.judge(t, earns, cost,
                feed, e.planner().city(e, plans, game), null);
        return new Weighed(earns, cost, vans, fleet, e.planner().hurdle(e, plans, game, cost), c.failed(), c.why());
    }

    /**
     * A works' own power (0.7.142): the kW it makes (BuildingsTemplate
     * .getGen()), its draw, what it buys past what it makes and what it
     * sells the grid past its draw (powerBought(), powerSold()), and what
     * that sells for a month at its sector's tariff (ProcessingSector
     * .tariff(), the price Sector.getPowerSoldValue() strikes the surplus
     * at). Null for a building that makes none.
     */
    public record Power(double gen, double draw, double bought, double sold, double soldValue) { }

    public static Power power(Game game, BuildingsTemplate t) {
        if (t == null || !(t.getGen() > 0)) return null;
        Sector owner = game.getSectors().ownerOf(t);
        double tariff = owner instanceof ham.citybuildersim.sectors.ProcessingSector p ? p.tariff() : Double.NaN;
        return new Power(t.getGen(), t.getElectricityConsumption(), t.powerBought(), t.powerSold(), t.powerSold() * tariff);
    }

    /**
     * The city's old metal a yard buys (0.7.142; OldMetal, the spec's 2.9):
     * the metal (STEEL for a Scrap Yard, COPPER for a Scrap Copper
     * Refinery), what one takes a month at nameplate (BuildingsTemplate
     * .oldMetal()), what its owner pays a tonne today (OldMetal.PAY_SHARE of
     * its scrap's export price: HeavyIndustry.oldSteelPrice(),
     * CopperRefining.oldCopperPrice()), what arose in the pool's last month
     * (OldMetal.Pool.lastArisen()), what the owner's yards standing take of
     * it at nameplate (oldSteelIntake(), oldCopperIntake()) and what arose
     * past that - what one more would run on. Null for a building that
     * takes none.
     */
    public record OldMetalBought(Good metal, double intake, double price, double arisen, double standingTake, double spare) { }

    public static OldMetalBought oldMetal(Game game, BuildingsTemplate t) {
        if (t == null || t.oldMetalTaken().isEmpty() || game.getOldMetal() == null) return null;
        Good metal = t.oldMetalTaken().keySet().iterator().next();
        double intake = t.oldMetal(metal);
        double price, standing;
        OldMetal.Pool pool;
        if (metal == Good.COPPER) {
            ham.citybuildersim.sectors.CopperRefining c = game.getSectors().copperRefining();
            price = c.oldCopperPrice();
            standing = c.oldCopperIntake();
            pool = game.getOldMetal().copper();
        } else {
            ham.citybuildersim.sectors.HeavyIndustry h = game.getSectors().heavyIndustry();
            price = h.oldSteelPrice();
            standing = h.oldSteelIntake();
            pool = game.getOldMetal().steel();
        }
        double arisen = pool.lastArisen();
        return new OldMetalBought(metal, intake, price, arisen, standing, Math.max(0, arisen - standing));
    }

    /** One sector here that buys a good (0.7.142): what its standing buildings take of it a month at nameplate (Sector.getInputAtCapacity()) - none while none stands. */
    public record Buyer(Sector sector, double takes) { }

    /**
     * Every sector here whose catalogue holds a building that uses `g`, in
     * Sectors' order, with what its standing buildings take a month at
     * nameplate (0.7.142): who could buy the good in this city. Empty when
     * nothing here can - what ships until a buyer comes (spec-materials
     * 2.12: lumber, panels, paper and pellets, cement, aggregate, dimension
     * stone and thermal coal, today). Households' basket is not a building
     * and not counted.
     */
    public static List<Buyer> buyers(Game game, Good g) {
        List<Buyer> out = new ArrayList<>();
        BuildingManager b = game.getBuildingManager();
        for (Sector s : game.getSectors().all()) {
            boolean uses = false;
            for (BuildingsTemplate t : b.getTemplatesBySector(s.key())) {
                if (t.uses(g) > 0) { uses = true; break; }
            }
            if (uses) out.add(new Buyer(s, s.getInputAtCapacity(g)));
        }
        return out;
    }

    /**
     * What one of a materials building makes of a good, and where it goes
     * (0.7.142): its tonnes a month at nameplate (Stands.makes() for a
     * building on sites, its row's for any other), whether its owner's own
     * works take it first (Sector.captive(): a ProcessingSector's every good
     * it both makes and uses - Wood Products' residues and pulp, Iron &
     * Steel's coke, pig iron and scrap) and what they take of it at
     * nameplate, and the other sectors here that buy it (buyers(), the owner
     * left out) - none, and not captive, when it ships.
     */
    public record Outlet(Good good, double made, boolean captive, double ownTakes, List<Buyer> others) {

        /** Whether nothing here buys it: no other sector, and its owner does not take it first. */
        public boolean ships() { return !captive && others.isEmpty(); }
    }

    /** ...each good one makes, in its makes' order; empty for a building that is not a materials one. */
    public static List<Outlet> outlets(Game game, BuildingsTemplate t) {
        List<Outlet> out = new ArrayList<>();
        if (!materials(game, t)) return out;
        Sector owner = game.getSectors().ownerOf(t);
        Stands at = stands(game, t);
        Map<Good, Double> makes = at != null ? at.makes() : t.goodsMade();
        for (Map.Entry<Good, Double> e : makes.entrySet()) {
            if (!(e.getValue() > 0)) continue;
            Good g = e.getKey();
            List<Buyer> others = new ArrayList<>();
            for (Buyer by : buyers(game, g)) if (by.sector() != owner) others.add(by);
            boolean captive = owner.captive(g);
            out.add(new Outlet(g, e.getValue(), captive, captive ? owner.getInputAtCapacity(g) : 0, others));
        }
        return out;
    }

    /**
     * A materials sector's chain as its Sectors page's lines (0.7.142; each of
     * the nine industries' ownLines() ends on them): its works' own power,
     * when any standing makes some - what they make (BuildingsTemplate
     * .getGen()), what the sector sells the grid past its draws
     * (Sector.getPowerSurplus()) and what that sells for a month
     * (getPowerSoldValue()); then WHO BUYS IT HERE, each good it makes in its
     * own order (Sector.goodsMade()): its own works first where they take it
     * (captive, with what they take at nameplate), each other sector here
     * that could buy it with what its standing buildings take at nameplate
     * (buyers()), or that nothing here buys it and it ships - outlets()'s
     * reading, for a sector rather than one building.
     */
    public static List<Sector.Line> chainLines(Game game, Sector sector) {
        List<Sector.Line> lines = new ArrayList<>();
        if (game == null || sector == null) return lines;
        Formats f = Formats.INSTANCE;
        BuildingManager b = game.getBuildingManager();
        double made = b.totalBySector(sector.key(), BuildingsTemplate::getGen);
        if (made > 0) {
            lines.add(Sector.Line.head("Its own power, a month"));
            lines.add(Sector.Line.of("Its works make", f.count(made) + " kW"));
            lines.add(Sector.Line.of("Sold to the grid, past their draw", f.count(Math.max(0, sector.getPowerSurplus())) + " kW · "
                    + f.amount(sector.getPowerSoldValue()), sector.getPowerSurplus() > 0 ? Sector.Line.Tone.GOOD : Sector.Line.Tone.NONE));
        }
        lines.add(Sector.Line.head("Who buys it here, at nameplate"));
        for (Good g : sector.goodsMade()) {
            List<String> who = new ArrayList<>();
            if (sector.captive(g)) {
                double own = sector.getInputAtCapacity(g);
                who.add("its own works first" + (own > 0 ? ", " + f.units(own, g) : ""));
            }
            for (Buyer by : buyers(game, g)) {
                if (by.sector() == sector) continue;
                who.add(by.sector().label() + (by.takes() > 0 ? " " + f.units(by.takes(), g) : ", none standing"));
            }
            lines.add(who.isEmpty() ? Sector.Line.of(g.label(), "nobody here: it ships", Sector.Line.Tone.MUTED)
                    : Sector.Line.of(g.label(), String.join(" · ", who)));
        }
        return lines;
    }

    /**
     * E, the crew rule (0.7.142; sectors.Forestry, E AND THE PLAN; spec-zoning
     * 3 and board 3): the forest Forestry may cut (LandManager
     * .cuttableForest()), every crew's cut a month standing and on site
     * (Forestry.crewsCut(true)), one more `t`'s (Forestry.cutOf()), the
     * months the forest lasts the crews now (infinite with none) and with one
     * more, whether it has room for one more (Forestry.roomFor()), how many
     * more of `t` it has room for, and how many of `t` it would carry alone -
     * each at FOREST_CREW_MONTHS of the cut.
     */
    public record CrewRule(double cuttable, double committed, double cut, double months, double monthsWith,
                           boolean room, int more, int carries) { }

    public static CrewRule crewRule(Game game, BuildingsTemplate t) {
        ham.citybuildersim.sectors.Forestry f = game.getSectors().forestry();
        double cuttable = game.getLandManager().cuttableForest(), committed = f.crewsCut(true);
        double cut = ham.citybuildersim.sectors.Forestry.cutOf(t);
        double horizon = ham.citybuildersim.sectors.Forestry.FOREST_CREW_MONTHS;
        int more = 0, carries = 0;
        if (cut > 0) {
            more = (int) Math.max(0, Math.floor((cuttable / horizon - committed) / cut));
            while (more > 0 && !(cuttable >= horizon * (committed + more * cut))) more--;
            while (cuttable >= horizon * (committed + (more + 1) * cut)) more++;
            carries = (int) Math.max(0, Math.floor(cuttable / (horizon * cut)));
        }
        return new CrewRule(cuttable, committed, cut, committed > 0 ? cuttable / committed : Double.POSITIVE_INFINITY,
                committed + cut > 0 ? cuttable / (committed + cut) : Double.POSITIVE_INFINITY,
                ham.citybuildersim.sectors.Forestry.roomFor(cuttable, committed, t), more, carries);
    }

    /** ...the rule as the forest law's page says it: for the smallest crew in the catalogue (the one E refuses last); null with none. */
    public static CrewRule crewRule(Game game) {
        BuildingsTemplate smallest = smallestCrew(game);
        return smallest == null ? null : crewRule(game, smallest);
    }

    /** The crew in the catalogue that cuts least a month (Forestry.cutOf()), the first on a tie; null with none. */
    public static BuildingsTemplate smallestCrew(Game game) {
        BuildingsTemplate best = null;
        for (BuildingsTemplate t : game.getBuildingManager().getTemplatesBySector(Sectors.FORESTRY)) {
            if (!ham.citybuildersim.sectors.Forestry.isCrew(t)) continue;
            if (best == null || ham.citybuildersim.sectors.Forestry.cutOf(t) < ham.citybuildersim.sectors.Forestry.cutOf(best)) best = t;
        }
        return best;
    }

    /** E's refusal in Forestry's plan's words (Forestry.eWords(), which BuildCardCheck 12 holds this to): the forest it may cut, and the months it would last with one more. */
    static String eWords(CrewRule rule, BuildingsTemplate t) {
        double with = rule.committed() + rule.cut();
        return String.format("the forest it may cut, %,.0fk m³, would last %.0f months of the crews' cut with %s; it asks %d",
                rule.cuttable() / 1000, with > 0 ? rule.cuttable() / with : 0, a(t.getName()),
                ham.citybuildersim.sectors.Forestry.FOREST_CREW_MONTHS);
    }

    /** "a Logging Camp", "an Iron Mine": ProcessingSector.a()'s article. */
    static String a(String name) {
        return (name != null && !name.isEmpty() && "AEIOU".indexOf(Character.toUpperCase(name.charAt(0))) >= 0 ? "an " : "a ") + name;
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
     * staffing test; LICENCE: licences; ORE: a deposit, the ore or (0.7.138)
     * the forest Forestry may cut; SUPPLY:
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
    /** ...ORE's: the ground has none - and since 0.7.138 Forestry's, the forest it may cut (none, or too little for E); since 0.7.142 a field of its type and a resource worked out, named as itself ("no coking coal left in the city's fields", "the coal is worked out"). */
    static final String[] ORE_WORDS     = { "deposit", "ore", "forest", "field", "fields", "worked out" };
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
            return new Investors(owner.label(), owner.key(), owner, theirs, yoursToo, onSite, months, word, null, Double.NaN, null);
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
