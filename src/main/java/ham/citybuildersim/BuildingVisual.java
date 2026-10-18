package ham.citybuildersim;

import java.util.Arrays;
import java.util.List;

/**
 * How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which kind, whether it stands on a resource's sites, and whether the map places it from the outside of the city in.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5 and 2.6,
 * star 10). Jerus's map mockup (city-map.html) drew ten kinds of building -
 * homes, shops, offices, industry, farms, utilities, schools, health, safety,
 * and mines and wells - each with its fill, its edge and its footprint (the
 * mockup's star 8), and the game has its building types in eighteen
 * categories. This is the one table between them, so the tile painter, the
 * raster, the districts' pyramid and the legend J4 draws all read one answer.
 * It is the picture only: nothing in the model reads it.
 *
 * WHAT IT DECIDES, AND FROM WHAT. A type's category gives its class; three
 * things are read off the template rather than its name, as the house rule
 * asks (BuildingsTemplate's care field): a road is an INFRASTRUCTURE type with
 * a road capacity and no transit, its kind by its freight grade (none gravel,
 * some paved, all a highway); flats are the homes with more than one dwelling
 * (the apartment types, the mockup's star 9 by type, not by people); a mine
 * or well stands on the sites of the resource whose good it makes (since
 * 0.8.0 the sites its template declares: THE MATERIALS DRAWN AS THEMSELVES,
 * below). One type
 * is named by its permanent id, home care (22), drawn as a home because it
 * is run from homes (spec-land star 10); id 15 was the home daycare until
 * 0.7.71 made it the Small Childcare Centre, a centre, drawn as care.
 *
 * WHAT A TILE DRAWS (0.7.64, batch L2; J3b's visual counts gone): one
 * building for every model building, of its type, on the type's own land
 * (FOOTPRINTS below) - a Low-Rise Apartments block one block of 2 x 3 plots,
 * not a street of houses; a kind the city has none of is not drawn.
 *
 * RAIL AS TRACK (0.7.72, batch N3). Jerus: "rail you add must connect to
 * the rail network, and rail terminals prefer to sit near mines." The
 * railway's lines - the Rail Spur and the Freight Line, a RAIL type that
 * is not a terminal - are drawn as track, their own land in plots laid
 * along a line (track()), as a road's are; a Rail Terminal (by its
 * permanent id, RAIL_TERMINALS) is a building, a yard beside its track.
 *
 * THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; spec-materials 5's
 * I14 row). The nine materials industries drew as two kinds: a mine on the
 * first site of its resource or, for every other mine, quarry, camp and
 * plantation, a plain grey "Mines and wells" box, and every works a plain
 * violet "Industry" box. Now a type standing on a resource's sites says so
 * from the template's own site (BuildingsTemplate.site(): its resource,
 * the type of field and how many sites it takes - a Large Open-Pit Mine
 * twenty), and each kind of building has a look of its own (LOOKS): an open
 * pit, an underground mine, a quarry, a leach plant, a coal wash plant, a
 * logging camp, planted forest - and each processing industry's works, of
 * one colour and placed as one campus (GROUPS: the refinery's units since
 * 0.7.97, then steel's, cement's, copper's and wood's). Still the picture
 * only: nothing in the model reads it.
 */
public final class BuildingVisual {

    private BuildingVisual() { }

    /* =====================================================================
       THE TEN CLASSES (the mockup's TYPES, in its order)
       ===================================================================== */

    /** Homes: houses, and the flats drawn darker. */
    public static final int HOME = 0;
    /** Shops: convenience and grocery stores, the bank's branches, boutiques, diners. */
    public static final int SHOP = 1;
    /** Offices: business services. */
    public static final int OFFICE = 2;
    /** Industry: bakeries to steel, construction, the railway and the car plants. */
    public static final int INDUSTRY = 3;
    /** Farms: on open grass only. */
    public static final int FARM = 4;
    /** Utilities: power, water and the transit depots. */
    public static final int UTILITY = 5;
    /** Schools, colleges and universities. */
    public static final int SCHOOL = 6;
    /** Health: clinics, hospitals, care and the cemeteries. */
    public static final int HEALTH = 7;
    /** Safety: police and the jails. */
    public static final int SAFETY = 8;
    /** Mines and wells: on their resource's sites. */
    public static final int MINE = 9;

    /** How many classes: ten - what the pyramid sums a node's buildings by (spec-land 2.5). */
    public static final int CLASSES = 10;

    /** The classes' names, as the legend writes them (the mockup's). */
    public static final String[] CLASS_NAMES = { "Homes", "Shops", "Offices", "Industry", "Farms", "Utilities",
            "Schools", "Health", "Safety", "Mines and wells" };

    /** Each class's fill, 0xAARRGGBB - the mockup's TYPES. */
    public static final int[] FILL = { 0xffeaa572, 0xfff2c94c, 0xff5b8fd6, 0xff9076ba, 0xffe0cb84, 0xff2ea89e,
            0xffd0587a, 0xfff7f4ee, 0xffd8442f, 0xff8e9093 };

    /** ...and its edge, drawn from 6 px a plot (the mockup's). */
    public static final int[] EDGE = { 0xff9a5a2c, 0xff987718, 0xff2c5590, 0xff55407a, 0xffa99550, 0xff17635d,
            0xff86304a, 0xff8f8a82, 0xff86251a, 0xff3d3f42 };

    /** Flats' fill: the mockup's FLATS, a darker orange than a house. */
    public static final int FLATS_FILL = 0xffd07a44;

    /** ...and their edge. */
    public static final int FLATS_EDGE = 0xff7e4119;

    /** The refinery's units (0.7.97, campus()): mockup 3's refinery, #4a3f5c, a darker violet than industry's, so the campus reads as one... */
    public static final int CAMPUS_FILL = 0xff4a3f5c;

    /** ...edged in its #7a6a90. */
    public static final int CAMPUS_EDGE = 0xff7a6a90;

    /** A sea terminal's apron (0.7.97, berth()): mockup 3's quays' grey, #2e3640... */
    public static final int TERMINAL_FILL = 0xff2e3640;

    /** ...edged in its #4a5866. */
    public static final int TERMINAL_EDGE = 0xff4a5866;

    /** A tank farm's tanks (0.7.97, the refiners' Tank Farm and the city's Strategic Reserve): mockup 3's tanks, #2a3540... */
    public static final int TANKS_FILL = 0xff2a3540;

    /** ...ringed in its #7f8ca6. */
    public static final int TANKS_EDGE = 0xff7f8ca6;

    /* ------------------------------------- the materials' looks (0.8.0, batch I14) */

    /** A type's look: as its class draws it, a box of its class's colours (every type but the materials'). */
    public static final int PLAIN = 0;
    /** ...an open pit on its sites: benches stepping down to its floor, in its resource's colour (the iron mines, the surface coal mines, the sulphide copper mines). */
    public static final int PIT = 1;
    /** ...an underground mine on its site: the site's yard and spoil, its headframe in the middle (the coking coal's mine). */
    public static final int SHAFT = 2;
    /** ...a quarry on its sites: straight benches cut in the stone (the three quarries). */
    public static final int QUARRY = 3;
    /** ...a leach plant on its site: its heaps in rows and its pond (the oxide copper's Leach & SX-EW Plant). */
    public static final int LEACH = 4;
    /** ...a coal wash plant: a dark works with its conveyor (the Coal Preparation Plant). */
    public static final int WASH = 5;
    /** ...a logging camp: a clearing and its log stacks (Forestry's crews). */
    public static final int CAMP = 6;
    /** ...planted forest: trees in rows on its ground (the Tree Plantation and the Woodlot). */
    public static final int STAND = 7;
    /** ...a processing works: its industry's own colours (GROUP_FILL), placed with the rest of its industry's as one campus. */
    public static final int WORKS = 8;

    /** How many looks. */
    public static final int LOOKS = 9;

    /** Each look as the legend names it (PLAIN and WORKS by their class and their industry instead). */
    public static final String[] LOOK_NAMES = { "", "Open pit", "Underground mine", "Quarry", "Leach plant", "Coal wash plant",
            "Logging camp", "Planted forest", "Works" };

    /** Each look's fill, 0xAARRGGBB: a pit's and a quarry's from their resource's colour (pitColours()), the rest their own - a shaft's yard, the heaps' green, the wash plant's coal, the camp's sawdust, the plantation's darker forest. */
    public static final int[] LOOK_FILL = { 0, 0xff6e5a4a, 0xffa39783, 0xffcfc8b4, 0xff5fae93, 0xff3a3a42, 0xffb58c5c, 0xff3f6a36, 0 };

    /** ...and its edge from 6 px a plot. */
    public static final int[] LOOK_EDGE = { 0, 0xff3d3129, 0xff5c5347, 0xff8d8673, 0xff2f6f5b, 0xff17171b, 0xff6d4c2a, 0xff2c4d26, 0 };

    /** A works' campus (GROUPS): none... */
    public static final int NO_GROUP = 0;
    /** ...the refinery's units (0.7.97, campus())... */
    public static final int REFINERY = 1;
    /** ...Iron & Steel's works (sectors.HeavyIndustry: the arc furnaces and mini-mills, the Scrap Yard, the integrated route)... */
    public static final int STEEL = 2;
    /** ...Cement & Concrete's kilns and cement plants... */
    public static final int CEMENT = 3;
    /** ...Copper Refining's smelters, refineries and rod mills... */
    public static final int COPPER_WORKS = 4;
    /** ...and Wood Products' sawmills and mills. */
    public static final int WOOD = 5;

    /** How many groups, NO_GROUP among them. */
    public static final int GROUPS = 6;

    /** The sector each processing works' group is (Sectors' keys), by group; none for NO_GROUP and the refinery's, which campus() reads. */
    static final String[] GROUP_SECTORS = { null, null, Sectors.HEAVY_INDUSTRY, Sectors.CEMENT_CONCRETE, Sectors.COPPER_REFINING,
            Sectors.WOOD_PRODUCTS };

    /** Each group's works as the legend names them. */
    public static final String[] GROUP_NAMES = { "", "Refinery", "Iron & steel works", "Cement works", "Copper works", "Wood mills" };

    /** Each group's fill: the refinery's CAMPUS_FILL, steel's rust, cement's grey, copper's own and the mills' timber. */
    public static final int[] GROUP_FILL = { 0, CAMPUS_FILL, 0xff6b4a42, 0xffb4b0a6, 0xffb0663a, 0xff8a6a44 };

    /** ...and its edge from 6 px a plot. */
    public static final int[] GROUP_EDGE = { 0, CAMPUS_EDGE, 0xffa0705c, 0xff76726a, 0xff6e3b1d, 0xff5a4128 };

    /* --------------------------------------------------------- road kinds */

    /** Not a road. */
    public static final int NOT_A_ROAD = 0;
    /** A gravel road: no freight grade. */
    public static final int GRAVEL = 1;
    /** A paved road: a freight grade under one. */
    public static final int PAVED = 2;
    /** An elevated highway: a freight grade of one. */
    public static final int HIGHWAY = 3;

    /** The permanent ids of the care types drawn in the homes' colour, because they are run from homes (spec-land star 10): Home Care Service (22) - still drawn, one for one, on its own land (0.7.64). Home Daycare (15) was the other until 0.7.71 made id 15 an 80-place centre. */
    static final int[] DRAWN_AS_HOMES = { 22 };

    /** The permanent ids of the rail types drawn as a yard beside the track, not as track (0.7.72): the Rail Terminal (64). Every other RAIL type - the Rail Spur (62), the Freight Line (63) - is a line, laid plot by plot. */
    static final int[] RAIL_TERMINALS = { 64 };

    /** The permanent ids of the railway lines whose track makes the main line double (0.8.1, batch FR0; spec-freight 4.3): the Freight Line (63). */
    static final int[] FREIGHT_LINES = { 63 };

    /** Whether the type is one of FREIGHT_LINES: a railway line whose presence doubles the main line. */
    static boolean freightLine(Type t) {
        if (t == null || !t.track()) return false;
        for (int id : FREIGHT_LINES) if (t.id() == id) return true;
        return false;
    }

    /** The permanent ids of the works drawn in their group's colours but placed as any industry, not gathered into its campus (0.8.0, batch I14): Iron & Steel's Electric Arc Furnace (11) and Steel Mini-Mill (12), the scrap route every city had before 0.7.123's integrated one - placed already in every saved city's map and in the district plan's port of the prototype (PlanCheck 1), which a campus would move. */
    static final int[] NOT_GATHERED = { 11, 12 };

    /** Square feet in a plot: 30 m squared in square feet, 9,687.5 - what a template's land is turned into plots by. */
    public static final double SQ_FT_PER_PLOT = World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2;

    /* =====================================================================
       ONE TYPE
       ===================================================================== */

    /**
     * One building type as the map draws it.
     *
     * @param id        the template's permanent id
     * @param category  its category
     * @param cls       its class, HOME to MINE
     * @param flats     a home of more than one dwelling, drawn darker
     * @param road      its road kind, NOT_A_ROAD to HIGHWAY
     * @param site      the resource whose sites it stands on, or null
     * @param outer     placed from the outside of the city in: farms, utilities, mines and wells, rail and car plants (spec-land 2.5)
     * @param plots     its land in plots: its footprint (footprint()), and a road's plots
     * @param transit   a transit network: its depots and yards, drawn as a utility on its own land
     * @param sqFt      its land in whole square feet: the ground used a district and the pyramid sum, exactly
     * @param people    the people a home of it holds (its capacity; 0 for anything but a home)
     * @param jobs      the jobs it holds
     * @param track     a railway line, drawn as track plot by plot (0.7.72): a RAIL type not in RAIL_TERMINALS
     * @param terminal  a rail yard, drawn as a building beside its track (0.7.72): RAIL_TERMINALS
     * @param sea       a building at sea (0.7.91): an offshore platform, its wells, a crude pipeline
     *                  (BuildingsTemplate.standsAtSea()) - on none of the city's dry ground, so neither
     *                  dealt to a district nor drawn on the land; since 0.7.97 drawn on their field
     *                  (CityMap's THE OIL AT SEA, TilePainter's AT SEA)
     * @param campus    one of the refinery's units (0.7.97, batch O13; spec-oil 2.12, Q11): a crude unit
     *                  or a conversion unit (sectors.Refining.isCrudeUnit(), isConversionUnit()) - dealt
     *                  to the district holding the refinery's (CityMap's THE CAMPUS) and packed together
     *                  there, first of its industry (DistrictPlan's THE CAMPUS)
     * @param shore     a work on the shore (0.7.97; spec-roads-and-ports.md 2.8: "ports and tank farms ...
     *                  on the shore"): a sea terminal (BuildingsTemplate.isPort()), the refiners' Tank
     *                  Farm or the city's Strategic Reserve - laid city-wide on owned dry ground at owned
     *                  sea (CityShore), not by its district's plan
     * @param berth     a terminal's cargo, Ports.Cargo's ordinal (0.7.97): its quay runs out over the sea
     *                  and its boats call there; -1 for anything else
     * @param sites     the sites of `site` it takes (0.8.0, batch I14): the template's own (BuildingsTemplate.site()) -
     *                  a Large Open-Pit Mine twenty, so it is drawn on twenty sites; 0 with no site
     * @param field     the type of field it stands on (0.8.0): the template's, null for any of its resource's
     * @param look      its look (0.8.0): PLAIN, or a materials building's own, PIT to WORKS
     * @param group     its works' group (0.8.0): NO_GROUP, the REFINERY's (campus) or a processing
     *                  industry's, STEEL to WOOD - its colours
     * @param gathered  placed in its group's campus (0.8.0): its units dealt to one district and packed in
     *                  touching cells (campusGroup()) - every works of a group but NOT_GATHERED's
     * @param sector    the sector that owns it (0.8.0; BuildingsTemplate.getSector(), "" for none): whose zone
     *                  its district is tinted for (CityMap's ZONES BY DISTRICT)
     */
    public record Type(int id, BuildingType category, int cls, boolean flats, int road, Resource site,
                       boolean outer, double plots, boolean transit, long sqFt, int people, int jobs,
                       boolean track, boolean terminal, boolean sea, boolean campus, boolean shore, int berth,
                       int sites, Deposit.FieldType field, int look, int group, boolean gathered, String sector) {

        /** The campus it is placed in (0.8.0): its group where it is gathered, else NO_GROUP - what the city map and the district plan place by. */
        public int campusGroup() { return gathered ? group : NO_GROUP; }

        /** Whether it is drawn as a building: not a road, nor a railway line (0.7.72), nor at sea (0.7.91). */
        public boolean drawn() { return road == NOT_A_ROAD && !track && !sea; }

        /** Its fill: its class's, the flats darker, and since 0.7.97 the refinery's units, a terminal and a tank farm their own (still of their class's in the blocks and the legend's ten); since 0.8.0 a works its group's and a materials building its look's. */
        public int fill() {
            return flats ? FLATS_FILL : group > NO_GROUP ? GROUP_FILL[group] : berth >= 0 ? TERMINAL_FILL : shore ? TANKS_FILL
                    : look != PLAIN ? lookFill(this) : FILL[cls];
        }

        /** Its edge. */
        public int edge() {
            return flats ? FLATS_EDGE : group > NO_GROUP ? GROUP_EDGE[group] : berth >= 0 ? TERMINAL_EDGE : shore ? TANKS_EDGE
                    : look != PLAIN ? lookEdge(this) : EDGE[cls];
        }

        /** Whether it is one of the materials industries' own (0.8.0): drawn by a look of its own, not its class's box. */
        public boolean materials() { return look != PLAIN; }
    }

    /** A template as the map draws it. */
    public static Type of(BuildingsTemplate t) {
        BuildingType c = t.getCategory();
        int id = t.getId();
        double plots = Math.max(0, t.getLandSqFt()) / SQ_FT_PER_PLOT;
        int road = NOT_A_ROAD;
        if (c == BuildingType.INFRASTRUCTURE && !t.isTransit() && t.getCapacity() > 0) {
            double g = t.getFreightGrade();
            road = g <= 0 ? GRAVEL : g < 1 ? PAVED : HIGHWAY;
        }
        Resource site = null;
        boolean sea = t.standsAtSea();
        // On the sites the template declares (0.8.0, batch I14; BuildingsTemplate.site()): its resource in fields, the
        // sites it takes - every mine, quarry and leach plant, the Dimension Stone Quarry and the copper concentrators
        // among them, which the good they make (the rule until 0.7.143) missed. Forest's crews cut terrain, on no site.
        BuildingsTemplate.Site declared = t.site();
        int sites = 0;
        Deposit.FieldType field = null;
        if (!sea && declared != null && declared.sites() > 0 && declared.resource() != null && declared.resource().inFields()) {
            site = declared.resource();
            sites = declared.sites();
            field = declared.type();
        }
        boolean flats = c == BuildingType.RESIDENTIAL && t.getDwellings() > 1;
        int cls = classOf(c);
        for (int h : DRAWN_AS_HOMES) if (h == id) cls = HOME;
        boolean outer = c == BuildingType.AGRICULTURE || c == BuildingType.ELECTRICITY || c == BuildingType.WATER
                || c == BuildingType.MINING || c == BuildingType.RAIL || c == BuildingType.AUTOMOTIVE;
        int people = c == BuildingType.RESIDENTIAL ? Math.max(0, t.getCapacity()) : 0;
        boolean terminal = false;
        for (int r : RAIL_TERMINALS) if (r == id) terminal = true;
        boolean track = c == BuildingType.RAIL && !terminal;
        boolean campus = ham.citybuildersim.sectors.Refining.isCrudeUnit(t) || ham.citybuildersim.sectors.Refining.isConversionUnit(t);
        boolean shore = t.isPort() || ham.citybuildersim.sectors.Refining.isTankFarm(t) || StrategicReserve.isReserve(t);
        int berth = t.isPort() ? t.berthCargo().ordinal() : -1;
        int group = campus ? REFINERY : NO_GROUP;
        for (int g = STEEL; g < GROUPS; g++) if (GROUP_SECTORS[g].equals(t.getSector()) && c == BuildingType.HEAVY_INDUSTRY) group = g;
        boolean gathered = group != NO_GROUP;
        for (int n : NOT_GATHERED) if (n == id) gathered = false;
        int look = lookOf(t, site, field, group);
        return new Type(id, c, cls, flats, road, site, outer, plots, t.isTransit(), Math.round(Math.max(0, t.getLandSqFt())),
                people, Math.max(0, t.getTotalJobs()), track, terminal, sea, campus, shore, berth, sites, field, look, group,
                gathered, t.getSector());
    }

    /**
     * A type's look (0.8.0, batch I14), off the template as the house rule
     * asks: on a resource's sites, iron's, coal's and copper's mines an open
     * pit - but coking coal's, which is mined underground, a shaft, and
     * copper's oxide field's, which is leached at the mine, a leach plant -
     * and stone's a quarry; coal's works on no site, which washes, the wash
     * plant; Forestry's buildings that cut a camp and those that make
     * nothing, its stands, planted forest; a processing industry's works its
     * group's. Oil's wells, and everything else, PLAIN.
     */
    static int lookOf(BuildingsTemplate t, Resource site, Deposit.FieldType field, int group) {
        if (site == Resource.IRON) return PIT;
        if (site == Resource.COAL) return field == Deposit.FieldType.COKING ? SHAFT : PIT;
        if (site == Resource.COPPER) return field == Deposit.FieldType.OXIDE ? LEACH : PIT;
        if (site == Resource.STONE) return QUARRY;
        if (site == null && t.getWash() > 0) return WASH;
        if (Sectors.FORESTRY.equals(t.getSector())) return t.goodsMade().isEmpty() ? STAND : CAMP;
        if (group > REFINERY) return WORKS;
        return PLAIN;
    }

    /** A look's fill for a type: a pit's floor and a quarry's stone from its resource's colour, the rest the look's own. */
    static int lookFill(Type t) {
        if (t.look() == PIT && t.site() != null) return pitColours(t.site())[0];
        return LOOK_FILL[t.look()];
    }

    /** ...and its edge: a pit's rim. */
    static int lookEdge(Type t) {
        if (t.look() == PIT && t.site() != null) return pitColours(t.site())[2];
        return LOOK_EDGE[t.look()];
    }

    /** The dust a pit's benches are blended toward: dry spoil, #b8a58a. */
    static final int PIT_DUST = 0xffb8a58a;

    /** ...and the shade its floor is: #2a2620. */
    static final int PIT_SHADE = 0xff2a2620;

    /** A pit's colours from its resource's: {its floor, its benches, its rim} - the ore's colour darkened toward PIT_SHADE, blended halfway with PIT_DUST, and the dust - so an iron pit reads red, a coal pit black and a copper pit green. */
    public static int[] pitColours(Resource r) {
        int c = 0xff000000 | r.colour();
        return new int[] { TileRaster.blend(c, PIT_SHADE, 0.45), TileRaster.blend(c, PIT_DUST, 0.5), TileRaster.blend(PIT_DUST, PIT_SHADE, 0.35) };
    }

    /** A category's class (the mockup's ten). */
    public static int classOf(BuildingType c) {
        if (c == null) return INDUSTRY;
        switch (c) {
            case RESIDENTIAL:                                   return HOME;
            case COMMERCIAL: case LUXURY: case HOSPITALITY:     return SHOP;
            case BUSINESS_SERVICES:                             return OFFICE;
            case AGRICULTURE:                                   return FARM;
            case ELECTRICITY: case WATER: case INFRASTRUCTURE:  return UTILITY;
            case EDUCATION:                                     return SCHOOL;
            case HEALTHCARE:                                    return HEALTH;
            case SAFETY:                                        return SAFETY;
            case MINING:                                        return MINE;
            default:                                            return INDUSTRY;
        }
    }

    /**
     * Every type of a list of templates, indexed by id (null where no
     * template has that id): the table the map is drawn from.
     */
    public static Type[] table(List<BuildingsTemplate> templates) {
        int max = -1;
        for (BuildingsTemplate t : templates) max = Math.max(max, t.getId());
        Type[] out = new Type[max + 1];
        for (BuildingsTemplate t : templates) if (t.getId() >= 0) out[t.getId()] = of(t);
        return out;
    }

    /* =====================================================================
       FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2)

       Jerus, 2026-10-07: "the generation should only put what the city has,
       not more not less". J3 drew the mockup's footprints (industry 2 x 2 or
       3 x 2, farms 3 to 5 a side, the rest one plot) and J3b drew homes from
       people and workplaces from jobs, so the map showed buildings the city
       does not own and a Low-Rise Apartments block as a field of houses. A
       building is drawn now as the one building it is, on the ground the
       model gives its type: a House 0.83 of a plot (8,000 sq ft), Low-Rise
       Apartments 6.2 plots, a Coal Power Plant 206, a Livestock Farm 540 -
       so a city looks dense exactly where its buildings cover its land.
       ===================================================================== */

    /**
     * The whole plots a type is drawn on, {across, down}: its land in plots,
     * round(sqrt(plots)) across and round(plots / across) down, at least one
     * each way and never past a cell's interior (DistrictPlan.INTERIOR, 31;
     * a tile's 32 until 0.8.0, which no plan could hold: a Tree Plantation,
     * a Woodlot, a Large Copper Mine or a coal mine standing on no site threw
     * its district's plan - batch I14) - within half a plot a side of its own
     * ground (a House 1 x 1, Studio Apartments 2 x 1 for 2.6 plots, Low-Rise
     * Apartments 2 x 3 for 6.2, an Iron Mine 6 x 7 for 41.3, a Livestock Farm
     * 23 x 23 for 540). The same for every building of the type; the painter
     * turns it to face its road.
     */
    public static int[] footprint(Type t) {
        double p = Math.max(0, t.plots());
        int across = (int) Math.max(1, Math.min(DistrictPlan.INTERIOR, Math.round(Math.sqrt(p))));
        int down = (int) Math.max(1, Math.min(DistrictPlan.INTERIOR, Math.round(p / across)));
        return new int[] { across, down };
    }

    /** The whole plots a type is drawn on: its footprint's, or a road's or a railway line's own plots rounded (a Gravel Road 46, a Paved Road 26, an Elevated Highway 7; a Rail Spur 72, a Freight Line 248) - what a district's room and a tile's are kept in. */
    public static int cells(Type t) {
        if (t == null) return 0;
        if (t.road() != NOT_A_ROAD || t.track()) return (int) Math.max(1, Math.round(t.plots()));
        int[] f = footprint(t);
        return f[0] * f[1];
    }

    /**
     * The order buildings are dealt to a district's tiles and placed on a
     * tile: the largest footprint first (cells()), then by type id - every
     * drawn type of the table by rank. The deal and the painter take the one
     * order, so what the deal fits on a tile the painter can place there, and
     * a building added displaces only the smaller ones after it.
     */
    public static int[] rankedTypes(Type[] types) {
        return orderOf(types)[0];
    }

    /** ...and each type's whole plots, by id (cells()): the table's order and plots worked out once a table, the painter's every tile. */
    public static int[] cellsById(Type[] types) {
        return orderOf(types)[2];
    }

    /** ...and each type's footprint by id, {across[], down[]} (footprint()). */
    public static int[][] footprintsById(Type[] types) {
        int[][] o = orderOf(types);
        return new int[][] { o[3], o[4] };
    }

    private static final java.util.Map<Type[], int[][]> ORDERS = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /** A table's {order, ranks, cells, across, down}, kept by the table itself. */
    private static int[][] orderOf(Type[] types) {
        int[][] got = ORDERS.get(types);
        if (got != null) return got;
        int[] order = rank(types), ranks = new int[types.length], cells = new int[types.length];
        int[] across = new int[types.length], down = new int[types.length];
        Arrays.fill(ranks, -1);
        for (int k = 0; k < order.length; k++) ranks[order[k]] = k;
        for (int t = 0; t < types.length; t++) {
            cells[t] = cells(types[t]);
            if (types[t] == null) continue;
            int[] f = footprint(types[t]);
            across[t] = f[0];
            down[t] = f[1];
        }
        got = new int[][] { order, ranks, cells, across, down };
        ORDERS.put(types, got);
        return got;
    }

    private static int[] rank(Type[] types) {
        Integer[] ids = new Integer[types.length];
        int n = 0;
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].drawn()) ids[n++] = t;
        Arrays.sort(ids, 0, n, (a, b) -> cells(types[a]) != cells(types[b]) ? Integer.compare(cells(types[b]), cells(types[a]))
                : Integer.compare(a, b));
        int[] out = new int[n];
        for (int k = 0; k < n; k++) out[k] = ids[k];
        return out;
    }

    /** ...and each type's rank in it, by id (-1 for a road or no type). */
    public static int[] placeRanks(Type[] types) {
        return orderOf(types)[1];
    }

    /** The side a one-plot building is drawn at, as a share of its plot: the square root of its land in plots, a House's 0.91 and a Convenience Store's 0.72, so its drawn area is its own; a whole plot at a plot or more. */
    public static double plotSide(Type t) {
        return t == null ? 1 : Math.sqrt(Math.max(0, Math.min(1, t.plots())));
    }
}
