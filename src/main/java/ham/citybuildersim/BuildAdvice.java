package ham.citybuildersim;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What the city should build next, and why: the Build tab's categories and
 * the measures its five city categories open on, each measure's figure
 * before and after an order by the model's own arithmetic, and the rule
 * behind the overview's WHAT WOULD HELP MOST (0.7.24).
 *
 * WHY. Jerus, starting the interface over one screen at a time with Build:
 * "when you start the game you start in residential so the player without
 * reading thinks he needs to building houses". Build opens on an overview
 * now - the city's job, what would help most, and what the market builds by
 * itself - and its first two rows are this class. It is ADVICE, NOT A MODEL
 * CHANGE: it reads the city, quotes through Game.quoteBuild() (the quote the
 * Build button charges) and places nothing; an order is placed only when the
 * player clicks, through the Build button's own path. Pure, so the harness
 * (BuildAdviceCheck) can hold it without the toolkit.
 *
 * THE RULE (0.7.51; the brief's section 4 at 0.7.24), in one paragraph:
 * take the automatic remit in its order (AutoBuilder.remit()) - a school above
 * the ladder listed only for the students the city would both get and hire
 * (CityNeeds.wanted(), listsSchool()) - and keep those a city-built building
 * answers and what is on site does not already keep ahead; for each, of the
 * buildings that serve its measure (for the roads, any road or line, and
 * the city's gravel roads paved beside them), the plan with the lowest
 * whole-life net cost (HW1, WorksValue: order, replacement land and running
 * for BUILD_BOND_YEARS, less physical output loss prevented and payroll
 * taxes returned). A plan that closes the need ranks before one that cannot.
 * Reserve headroom sizes the plan but cannot claim prevented output loss;
 * the same physical valuation appears on the Build card. The
 * count that keeps the need ahead at the demand it opens to, projected
 * the way the businesses project theirs (above the ladder, the students it
 * would get and hire by then, its feeder's graduates counted) and the player's reserve
 * past it - off NEEDS YOU's list there, and a served gauge at 100% of it -
 * after what is on site at its staffed capacity; on credit when its quote
 * is more than the cash the ones before leave, never cut to the cash; at
 * most three, one per need. Every judgement in it is named where it is made
 * below, and in the project's design notes for 0.7.24 and 0.7.51. A first
 * building where none stands is suggested however far past the need one
 * goes (Jerus's school at 0% that one would take to 20,000%): the overshoot
 * enters the plan's cost, it never stops one (0.7.101, HW1).
 */
public final class BuildAdvice {

    private BuildAdvice() { }

    /* =====================================================================
       THE CATEGORIES (0.7.24)

       The Build strip's fourteen, in its order: the five only the city
       builds, then the nine investors build too. Five were renamed as Jerus
       chose ("Yes, rename them"): Residential is Homes, Commercial Shops,
       Industrial Industry, Infrastructure Roads & transit (Infrastructure again since FR4, below), Services Offices -
       the Build category's label only; BuildingType, the sectors and the
       rail's Infrastructure tab keep theirs. Kept here, not in the window, so
       the harness can hold that every building sits in exactly one.

       INFRASTRUCTURE AND FREIGHT (FR4, 2026-10-19; spec-freight-and-
       infrastructure 5, board 4). The city's things on one strip, the
       businesses' on theirs. "Roads & transit" is Infrastructure again (its
       `was`), and gains what only the city builds and Industry held: the sea
       terminals (BuildingType.PORTS) and the Strategic Reserve - a
       HEAVY_INDUSTRY building by type, so it is filed by template
       (Category.holds(), categoryOf(BuildingsTemplate)) and never by its type;
       "Rail" is Freight (its `was`), still RAIL and HAULAGE: the rungs, the
       Rail Depot and the Truck Depot. Industry keeps only what investors
       build (the Tank Farm stays: it is Refining's), so its dot and its line
       are honest again; Vehicles holds the Heavy Truck Plant, an AUTOMOTIVE
       building, as it always did.

       THE MODEL DOES NOT MOVE. The identifiers ROADS and RAIL stay - a dozen
       files use them - and only their strings changed. Ports and the reserve
       are in a category the city builds, but they are in no remit: they have
       no ring (measuresOf() is what it was, the port measures stay on
       portMeasures()), and AutoBuilder.inRemit() excludes them by name, as it
       did by their being in a market category. Every other reader of
       cityBuilds() or categoryOf(BuildingType) answers for every building what
       it answered.
       ===================================================================== */

    /** The page Build opens on: the city's job, what would help most, and what the market builds. */
    public static final String OVERVIEW = "Overview";

    /**
     * One category of the strip.
     *
     * @param was        its label before 0.7.24 (before FR4 for the two renamed back
     *                   and forth: Infrastructure's "Roads & transit", Freight's
     *                   "Rail"), or null when it kept its own - a caller still
     *                   holding an old label lands in the same place
     * @param cityBuilds true for the five nobody builds but the city
     */
    public record Category(String name, String was, EnumSet<BuildingType> types, boolean cityBuilds) {
        /** A fresh set every call: EnumSet is mutable, and screens should not share one. */
        @Override public EnumSet<BuildingType> types() { return EnumSet.copyOf(types); }

        /**
         * Whether this building is on the category's page: its type is among the category's - but for the
         * Strategic Reserve, a HEAVY_INDUSTRY building the city alone builds, which is Infrastructure's and
         * not Industry's (FR4). The one building filed by what it is, not by its type.
         */
        public boolean holds(BuildingsTemplate t) {
            if (t == null) return false;
            if (StrategicReserve.isReserve(t)) return name.equals(ROADS);
            return types.contains(t.getCategory());
        }
    }

    /** The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. */
    public static final String UTILITIES = "Utilities", ROADS = "Infrastructure", HEALTHCARE = "Healthcare",
            EDUCATION = "Education", SAFETY = "Safety", HOMES = "Homes", SHOPS = "Shops", INDUSTRY = "Industry",
            OFFICES = "Offices", FARMS = "Farms", RAIL = "Freight", VEHICLES = "Vehicles",
            LUXURY = "Luxury shops", RESTAURANTS = "Restaurants";
    // ROADS and RAIL keep their identifiers (FR4, 2026-10-19: a dozen files use them) and changed their strings: the
    // category of the roads, the transit lines, the ports and the reserve is "Infrastructure" (was "Roads & transit"), and
    // Freight's, the rail and the haulage (was "Rail"). category() still answers to the old labels, through `was`.

    /** The strip, in its order: the city's five, then the market's nine. */
    public static List<Category> categories() {
        List<Category> out = new ArrayList<>();
        out.add(new Category(UTILITIES,   null, EnumSet.of(BuildingType.ELECTRICITY, BuildingType.WATER), true));
        // ...with the city's sea terminals, and its Strategic Reserve (Category.holds()), from Industry (FR4, spec-freight 5).
        out.add(new Category(ROADS,       "Roads & transit", EnumSet.of(BuildingType.INFRASTRUCTURE, BuildingType.PORTS), true));
        out.add(new Category(HEALTHCARE,  null, EnumSet.of(BuildingType.HEALTHCARE), true));
        out.add(new Category(EDUCATION,   null, EnumSet.of(BuildingType.EDUCATION), true));
        out.add(new Category(SAFETY,      null, EnumSet.of(BuildingType.SAFETY), true));
        out.add(new Category(HOMES,       "Residential", EnumSet.of(BuildingType.RESIDENTIAL), false));
        out.add(new Category(SHOPS,       "Commercial", EnumSet.of(BuildingType.COMMERCIAL), false));
        // ...less the city's sea terminals (0.7.86 to FR4) and its Strategic Reserve, Infrastructure's now: Industry is what
        // investors build, and the Tank Farm stays (it is Refining's). The reserve's type, HEAVY_INDUSTRY, is Industry's still.
        out.add(new Category(INDUSTRY,    "Industrial", EnumSet.of(BuildingType.INDUSTRIAL,
                BuildingType.HEAVY_INDUSTRY, BuildingType.MINING, BuildingType.CONSTRUCTION), false));
        out.add(new Category(OFFICES,     "Services", EnumSet.of(BuildingType.BUSINESS_SERVICES), false));
        out.add(new Category(FARMS,       null, EnumSet.of(BuildingType.AGRICULTURE), false));
        out.add(new Category(RAIL,        "Rail", EnumSet.of(BuildingType.RAIL, BuildingType.HAULAGE), false));
        out.add(new Category(VEHICLES,    null, EnumSet.of(BuildingType.AUTOMOTIVE), false));
        out.add(new Category(LUXURY,      null, EnumSet.of(BuildingType.LUXURY), false));
        out.add(new Category(RESTAURANTS, null, EnumSet.of(BuildingType.HOSPITALITY), false));
        return out;
    }

    /** A category by its name - or by its label before 0.7.24. Null for none (the Overview included). */
    public static Category category(String name) {
        if (name == null) return null;
        for (Category c : categories()) {
            if (c.name().equals(name) || name.equals(c.was())) return c;
        }
        return null;
    }

    /** The category a kind of building is in, by its type: the Strategic Reserve's is Industry's, whose it is not (categoryOf(BuildingsTemplate) knows). */
    public static Category categoryOf(BuildingType type) {
        for (Category c : categories()) if (c.types.contains(type)) return c;
        return null;
    }

    /** The category a building is on the page of (FR4): its type's, but for the Strategic Reserve, which is Infrastructure's (Category.holds()). */
    public static Category categoryOf(BuildingsTemplate t) {
        if (t == null) return null;
        for (Category c : categories()) if (c.holds(t)) return c;
        return null;
    }

    /** A category's buildings in catalogue order: those of its types it holds, and the reserve in Infrastructure's. */
    public static List<BuildingsTemplate> templatesOf(Category c, BuildingManager buildings) {
        List<BuildingsTemplate> out = new ArrayList<>();
        for (BuildingsTemplate t : buildings.getKnownTemplates()) if (c.holds(t)) out.add(t);
        return out;
    }

    /** The category a NEEDS YOU row's fix is in, for the five the city builds and Homes; null for the rest. */
    public static Category categoryOf(CityNeeds.Go go) {
        switch (go) {
            case UTILITIES:  return category(UTILITIES);
            case ROADS:      return category(ROADS);
            case HEALTHCARE: return category(HEALTHCARE);
            case EDUCATION:  return category(EDUCATION);
            case SAFETY:     return category(SAFETY);
            case HOMES:      return category(HOMES);
            default:         return null;
        }
    }

    /* =====================================================================
       THE MEASURES

       What a city category's page opens on: one ring per measure the
       category serves. Healthcare: general care, childcare, senior care and
       death care; Education: each level; Safety: police and cells; Infrastructure:
       road capacity and transit; Utilities: power and water. PLOTS
       has no ring - it is the burial plots' own NEEDS YOU row, which the
       death care ring's buildings answer. PORT (FR3) has none either: the
       berths of a kind of cargo, advised on their payback (A BERTH THAT PAYS
       BACK, at the end).

       STREETS AND HIGHWAYS, TWO MEASURES (FR7, 2026-10-18; Jerus's star 4).
       Infrastructure has three rings now: the STREETS (Kind.ROADS: the
       access road, the gravel and paved roads and the transit lines, judged
       on the streets' load over their capacity), the HIGHWAYS (Kind.HIGHWAYS:
       the Elevated Highway alone, judged on the through trips with no highway
       to take them, over what one highway carries at free flow - listed from
       CityNeeds.HIGHWAY_MIN_FILL of one, a highway not being built to stand
       idle) and transit. It was one ring and one winner: the roads replaced
       one another, never built side by side, and a highway came only at the
       top of the land price. Each need has its own row on NEEDS YOU
       (MAX_SUGGESTIONS already gives one card per need), so in a big city both
       come up, month after month, because both grow. RoadCheck's old section
       2, the one-pool ladder, is retired by his decision and not moved to let
       a change through. See InfrastructureManager, STREETS AND HIGHWAYS, TWO
       NEEDS.
       ===================================================================== */

    /**
     * The measures. ROADS is the STREETS need since FR7 (2026-10-18): the
     * access road, served by the gravel and paved roads and the transit
     * lines; HIGHWAYS, added on the end, is the through need, served by the
     * Elevated Highway alone (InfrastructureManager, STREETS AND HIGHWAYS,
     * TWO NEEDS). Each is judged on its own measure and has its own line on
     * NEEDS YOU (CityNeeds.Kind.ROADS and CityNeeds.Kind.HIGHWAYS). PORT (FR3,
     * docs/queue/FR3.md 5.4), last: the berths of one kind of cargo (A BERTH
     * THAT PAYS BACK, below).
     */
    public enum Kind { POWER, WATER, ROADS, TRANSIT, CARE, DEATH, PLOTS, SCHOOL, POLICE, CELLS, HIGHWAYS, PORT, GRID }

    /** One measure: a kind, and the care type or the school it is about - or, for PORT, the cargo (null for every other kind). */
    public record Measure(Kind kind, CareType care, EducationType school, Ports.Cargo cargo) {

        public static Measure of(Kind kind) { return new Measure(kind, CareType.NONE, EducationType.NONE, null); }
        public static Measure care(CareType care) { return new Measure(Kind.CARE, care, EducationType.NONE, null); }
        public static Measure school(EducationType school) { return new Measure(Kind.SCHOOL, CareType.NONE, school, null); }
        /** The berths of a kind of cargo (FR3): the sea terminals that handle it. */
        public static Measure port(Ports.Cargo cargo) { return new Measure(Kind.PORT, CareType.NONE, EducationType.NONE, cargo); }

        /** Whether this building serves the measure - the buildings its ring shows. */
        public boolean serves(BuildingsTemplate t) {
            if (t == null) return false;
            switch (kind) {
                case POWER:   return t.getCategory() == BuildingType.ELECTRICITY && !Substations.isSubstation(t);
                case GRID:    return Substations.isSubstation(t);
                case WATER:   return t.getCategory() == BuildingType.WATER;
                // The streets' roads: a highway gives no access and is the through need's (FR7).
                case ROADS:   return t.getCategory() == BuildingType.INFRASTRUCTURE && t.getCapacity() > 0 && !t.isHighway();
                case HIGHWAYS: return t.getCategory() == BuildingType.INFRASTRUCTURE && t.isHighway();
                case TRANSIT: return t.getCategory() == BuildingType.INFRASTRUCTURE && t.getTransitCapacity() > 0;
                case CARE:    return t.getCare() == care;
                case DEATH:   return t.getCare() == CareType.BURIAL || t.getCare() == CareType.CREMATION;
                case PLOTS:   return t.getCare() == CareType.BURIAL;
                case SCHOOL:  return t.getTeaches() == school;
                case POLICE:  return t.getSafety() == SafetyType.POLICE;
                case CELLS:   return t.getSafety() == SafetyType.PRISON;
                case PORT:    return cargo != null && Ports.isPort(t) && t.berthCargo() == cargo;
                default:      return false;
            }
        }

        /** The category its buildings are in. */
        public String category() {
            switch (kind) {
                case POWER: case WATER: case GRID: return UTILITIES;
                case ROADS: case HIGHWAYS: case TRANSIT: return ROADS;
                case CARE: case DEATH: case PLOTS: return HEALTHCARE;
                case SCHOOL:                   return EDUCATION;
                case PORT:                     return ROADS;        // the category that holds BuildingType.PORTS (Infrastructure since FR4)
                default:                       return SAFETY;
            }
        }

        /** Its name, as a ring and a heading say it. */
        public String label() {
            switch (kind) {
                case POWER:   return "Power";
                case GRID:    return "Power grid";
                case WATER:   return "Water";
                case ROADS:   return "Streets";
                case HIGHWAYS: return "Through traffic";
                case TRANSIT: return "Transit";
                case CARE:    return care == CareType.GENERAL ? "General care"
                                   : care == CareType.CHILDCARE ? "Childcare" : "Senior care";
                case DEATH:   return "Death care";
                case PLOTS:   return "Burial plots";
                case SCHOOL:  return school.getLabel();
                case POLICE:  return "Police";
                case PORT:    return cargo.label() + " berths";
                default:      return "Cells";
            }
        }

        /** True for a figure that is a load - the share of a network's capacity in use. */
        public boolean isLoad() { return kind == Kind.POWER || kind == Kind.WATER || kind == Kind.ROADS || kind == Kind.GRID; }
    }

    /** The measures a city category's page opens on, in its ring order; empty for a market category. */
    public static List<Measure> measuresOf(String category) {
        List<Measure> out = new ArrayList<>();
        Category c = category(category);
        if (c == null || !c.cityBuilds()) return out;
        switch (c.name()) {
            case UTILITIES:
                out.add(Measure.of(Kind.POWER));
                out.add(Measure.of(Kind.GRID));
                out.add(Measure.of(Kind.WATER));
                break;
            case ROADS:
                out.add(Measure.of(Kind.ROADS));
                out.add(Measure.of(Kind.HIGHWAYS));
                out.add(Measure.of(Kind.TRANSIT));
                break;
            case HEALTHCARE:
                out.add(Measure.care(CareType.GENERAL));
                out.add(Measure.care(CareType.CHILDCARE));
                out.add(Measure.care(CareType.SENIOR));
                out.add(Measure.of(Kind.DEATH));
                break;
            case EDUCATION:
                for (EducationType t : EducationType.values()) if (t != EducationType.NONE) out.add(Measure.school(t));
                break;
            default:
                out.add(Measure.of(Kind.POLICE));
                out.add(Measure.of(Kind.CELLS));
        }
        return out;
    }

    /** The measure a NEEDS YOU row is about, for the rows a city-built building answers; null for the rest. */
    public static Measure measureOf(CityNeeds.Need need) {
        if (need == null) return null;
        switch (need.kind()) {
            case POWER:         return Measure.of(Kind.POWER);
            case WATER:         return Measure.of(Kind.WATER);
            case ROADS:         return Measure.of(Kind.ROADS);
            case HIGHWAYS:      return Measure.of(Kind.HIGHWAYS);
            case CARE:          return Measure.care(need.care());
            case DEAD:          return Measure.of(Kind.DEATH);
            case PLOTS:         return Measure.of(Kind.PLOTS);
            case BASIC_SCHOOLS:
            case HIGHER_SCHOOL: return Measure.school(need.school());
            case CRIME:         return Measure.of(Kind.POLICE);
            case CELLS:         return Measure.of(Kind.CELLS);
            default:            return null;
        }
    }

    /**
     * The NEEDS YOU row that judges a measure - the worst of them, as the
     * panel would list them first, for death care whose two rows are the
     * dead and the plots - or null for a measure no row watches (transit, a
     * basic stage that is not the bottleneck, a school above the ladder
     * NEEDS YOU does not list - CityNeeds.listsSchool()).
     */
    public static CityNeeds.Need needFor(List<CityNeeds.Need> all, Measure m) {
        CityNeeds.Need found = null;
        for (CityNeeds.Need n : all) {
            Measure of = measureOf(n);
            if (of == null) continue;
            boolean mine = of.equals(m) || (m.kind() == Kind.DEATH && of.kind() == Kind.PLOTS);
            if (mine && (found == null || n.level() > found.level())) found = n;
        }
        return found;
    }

    /* =====================================================================
       STAFFED CAPACITY

       A building's capacity times the city's fill rate for its posts: the
       post-weighted average of each job type's fill, as the model weights it
       for care, schools and safety (BuildingManager.getStaffedCareCapacity()
       and its two siblings), so a building whose posts the city cannot fill
       serves less - and a city short of doctors prefers the building that
       needs fewer of them. A building with no posts is fully staffed.
       ===================================================================== */

    /** Σ posts x fill / Σ posts over a building's own posts; 1 with none - BuildingManager's per-building weighting. */
    public static double staffing(BuildingsTemplate t, double[] fill) {
        return staffing(NewUnit.standing(t), fill);
    }

    /** ...for one new building already resolved (QN1): its own posts. */
    public static double staffing(NewUnit u, double[] fill) {
        double posts = 0, staffed = 0;
        for (JobType job : JobType.values()) {
            int n = u.getJobs(job);
            if (n == 0) continue;
            posts += n;
            staffed += n * (fill != null && job.ordinal() < fill.length ? fill[job.ordinal()] : 1);
        }
        return posts > 0 ? staffed / posts : 1;
    }

    /**
     * The posts of a building the city likely cannot fill: Σ posts x (1 -
     * the city's fill rate for that job) over its own posts, 0 with none.
     * The Build card's short-staffed bar reads it per 10,000 of what the
     * building serves (0.7.24: Jerus's pick after the PC check, in place of
     * the scarcest job's posts alone). Shown, not ranked by: the rule reads
     * staffing().
     */
    public static double unfilledPosts(BuildingsTemplate t, double[] fill) {
        return unfilledPosts(NewUnit.standing(t), fill);
    }

    /** ...for one new building already resolved (QN1): its own posts. */
    public static double unfilledPosts(NewUnit u, double[] fill) {
        double unfilled = 0;
        for (JobType job : JobType.values()) {
            int n = u.getJobs(job);
            if (n == 0) continue;
            double f = fill != null && job.ordinal() < fill.length ? fill[job.ordinal()] : 1;
            unfilled += n * Math.max(0, 1 - f);
        }
        return unfilled;
    }

    /** Whether a building has any posts at all - a road has none, and its card says so instead of drawing a staff bar. */
    public static boolean hasPosts(BuildingsTemplate t) {
        return hasPosts(NewUnit.standing(t));
    }

    /** ...for one new building already resolved (QN1): its own posts. */
    public static boolean hasPosts(NewUnit u) {
        for (JobType job : JobType.values()) if (u.getJobs(job) > 0) return true;
        return false;
    }

    /**
     * What one building of this kind adds to a measure, in the measure's own
     * unit, at today's staffing: generation or treatment a month, people
     * cared for, places, officers, cells; for road capacity the trips it
     * takes off what is over the line (see roadsOver()); for transit the
     * riders its vehicles could carry, which the model does not discount by
     * staff. Burial plots are not discounted either (BuildingManager.
     * getCareCapacity() is the plots the model counts); a crematorium is.
     */
    public static double unit(Game game, Measure m, BuildingsTemplate t) {
        return unit(game, m, NewUnit.of(game, t));
    }

    /** ...of one new building already resolved (QN1): its own output and posts; the template it is of is what the measure serves. */
    public static double unit(Game game, Measure m, NewUnit u) {
        BuildingsTemplate t = u.template();
        // The streets' candidates are every road and line but a highway, which gives no access (FR7).
        if (!m.serves(t) && !(m.kind() == Kind.ROADS && t.getCategory() == BuildingType.INFRASTRUCTURE && !t.isHighway())) return 0;
        double[] fill = game.getPopulationManager().getJobFillRate();
        switch (m.kind()) {
            case GRID: return Substations.SUBSTATION_MW * 1000;
            case POWER:
                // A hydro station adds power only while a slot of the city's river is free (HY1: Game.hydroSlotsFree()).
                if (t.isHydro() && game.hydroSlotsFree(t) < 1) return 0;
                // ...and a steam plant only where the city owns water to cool it (PW2: Game.hasCoolingFor()).
                if (!game.hasCoolingFor(t)) return 0;
                // PW3, 2026-11-23: power is planned for the year's peak hour, so a wind or solar farm adds its credit there at the city's place,
                // falling with the variable rows' share with it standing (credit(), THE FIRM SUPPLY); every other row its production1.
                return u.getProduction1() * credit(game, t, variableShare(game, Map.of(t, 1L))) * firmFill(t, staffing(u, fill));
            case WATER: {
                /*
                 * THE WATER UNIT (0.7.59, batch J2): a fresh plant adds no
                 * more than the fresh water left under the city's limit
                 * (UtilitiesHandler.getFreshHeadroom()), so a capped city is
                 * offered desalination; a desalination plant adds its own
                 * where the city owns sea, and nothing where it cannot stand.
                 */
                double treats = u.getProduction1() * staffing(u, fill);
                if (t.isSeaWater()) return game.hasCoastFor(t, 1) ? treats : 0;
                return Math.min(treats, game.getServicesManager().getUtilitiesHandler().getFreshHeadroom());
            }
            case ROADS: {
                Map<BuildingsTemplate, Long> base = onSite(game, m);
                return roadsOver(game, base) - roadsOver(game, plus(base, t, 1));
            }
            case HIGHWAYS: {
                // The through trips one more highway takes off the streets: its capacity at free flow, or what overflows if that is less.
                Map<BuildingsTemplate, Long> base = onSite(game, m);
                return Math.min(t.getCapacity() * InfrastructureManager.FREE_FLOW, roads(game, base).getThroughOverflow());
            }
            case TRANSIT:
                return t.getTransitCapacity();
            case DEATH: case PLOTS:
                return t.getCare() == CareType.BURIAL ? t.getCapacity() : t.getCapacity() * staffing(u, fill);
            case PORT:
                // A berth's crews are Freight's (FR3), not the city's staff: its tonnes a month, fully worked.
                return Ports.berthMonth(t);
            default:
                return t.getCapacity() * staffing(u, fill);
        }
    }

    /** The same, fully staffed: what the building is built for. */
    public static double built(Game game, Measure m, BuildingsTemplate t) {
        return built(game, m, NewUnit.of(game, t));
    }

    /** ...of one new building already resolved (QN1): its own output. */
    public static double built(Game game, Measure m, NewUnit u) {
        BuildingsTemplate t = u.template();
        switch (m.kind()) {
            // PW1, 2026-11-11: a wind farm's firm kW, as unit() counts it (the card sets the one beside the other); PW3 its credit at the city's place.
            case POWER: return u.getProduction1() * credit(game, t, variableShare(game, Map.of(t, 1L)));
            case WATER: return u.getProduction1();
            case ROADS: case HIGHWAYS: return t.getCapacity();
            case TRANSIT:           return t.getTransitCapacity();
            case PORT:              return Ports.berthMonth(t);
            default:                return t.getCapacity();
        }
    }

    /* =====================================================================
       THE FIGURE, BEFORE AND AFTER

       Each measure's figure with buildings added to what stands, by the
       model's own arithmetic at today's staffing: the network's load, the
       road's utilisation (InfrastructureManager.with()), a coverage
       (Health.coverageOf(), Education's cover), crime at another police
       coverage (Crime.crimesAt()). With nothing added it is the figure the
       city stands at. The figure is the NEEDS YOU row's own where there is
       one: load, utilisation, coverage, the unburied, the months of plots,
       a basic stage's coverage, who would come and be hired over the seats
       (0.7.51), crime against Canada's, the caught not held. Against the
       demand an order is sized to (0.7.51, an Ahead), the demand side is
       today's times the projection and the slack; the supply side is as
       it stands.
       ===================================================================== */

    /** Whether a measure is one of the two road needs (FR7): the streets or the highways, which read the same network, so each counts every road on site. */
    public static boolean roadKind(Measure m) { return m.kind() == Kind.ROADS || m.kind() == Kind.HIGHWAYS; }

    /** Every unit on site of the buildings that serve a measure, for anybody's order. */
    public static Map<BuildingsTemplate, Long> onSite(Game game, Measure m) {
        Map<BuildingsTemplate, Long> out = new LinkedHashMap<>();
        for (BuildingsStacks site : game.getBuildingManager().getStacksUnderConstruction()) {
            BuildingsTemplate t = site.getBuilding();
            boolean counts = roadKind(m)
                    ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
            if (counts && site.getUnderConstruction() > 0) out.merge(t, site.getUnderConstruction(), Long::sum);
        }
        // ...and the gravel roads being paved (0.7.70; ConstructionControl,
        // F): each goes as its Paved Road on site opens, so the road once
        // what is on site opens is without them.
        long paving = game.pavingNow();
        BuildingsTemplate gravel = paving > 0 && roadKind(m)
                ? game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM) : null;
        if (gravel != null) out.merge(gravel, -paving, Long::sum);
        return out;
    }

    /** First pending unit's quoted opening at the current shares, including preparation owed before it (HW1). */
    public static double firstCompletion(Game game, Measure m, Map<BuildingsTemplate, Long> site) {
        double first = Double.POSITIVE_INFINITY;
        for (Map.Entry<BuildingsTemplate, Long> e : site.entrySet()) {
            BuildingsTemplate t = e.getKey();
            if (e.getValue() <= 0 || m.kind() == Kind.HIGHWAYS && !t.isHighway()) continue;
            BuildingsStacks stack = game.getBuildingManager().getStack(t);
            if (stack == null || stack.getUnderConstruction() <= 0) continue;
            double lead = game.getBuildingManager().siteUnitMonths(t, 1, game.getBuildingOutputAtEveryPost());
            if (Double.isFinite(lead)) first = Math.min(first, Math.ceil(lead));
        }
        return first;
    }

    /** Units on site, all told: the buildings going up (a gravel road a paving takes away is not one). */
    public static long units(Map<BuildingsTemplate, Long> added) {
        long n = 0;
        for (long v : added.values()) if (v > 0) n += v;
        return n;
    }

    /** A copy with n more of t. */
    public static Map<BuildingsTemplate, Long> plus(Map<BuildingsTemplate, Long> base, BuildingsTemplate t, long n) {
        Map<BuildingsTemplate, Long> out = new LinkedHashMap<>(base);
        if (n != 0) out.merge(t, n, Long::sum);
        return out;
    }

    /** ...and with every quantity of another added. */
    public static Map<BuildingsTemplate, Long> plus(Map<BuildingsTemplate, Long> base, Map<BuildingsTemplate, Long> more) {
        Map<BuildingsTemplate, Long> out = new LinkedHashMap<>(base);
        for (Map.Entry<BuildingsTemplate, Long> e : more.entrySet()) out.merge(e.getKey(), e.getValue(), Long::sum);
        return out;
    }

    /**
     * One entry of an `added` map as the figures below read it (QN1): a building to add (n > 0) as one new building of its
     * template would open now, a removal (n <= 0: the paving's gravel) as the template stands. The map keeps the template.
     */
    private static NewUnit adding(Game game, BuildingsTemplate t, long n) {
        return n > 0 ? NewUnit.of(game, t) : NewUnit.standing(t);
    }

    /** The measure's figure with these buildings standing as well. */
    public static double figure(Game game, Measure m, Map<BuildingsTemplate, Long> added) {
        return figure(game, m, added, NOW);
    }

    /** ...against the demand `p` says (0.7.51): today's times p.scale() - projected and padded - on the demand side only. */
    public static double figure(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        BuildingManager bm = game.getBuildingManager();
        double k = p.scale();
        switch (m.kind()) {
            case GRID: {
                double[] sd = supplyDemand(game, m, added, p);
                return sd[0] > 0 ? sd[1] / sd[0] : sd[1] > 0 ? Double.POSITIVE_INFINITY : 0;
            }
            case POWER: {
                // PW3: the year's peak hour's draw over the firm supply (THE FIRM SUPPLY, below).
                double[] sd = powerSupplyDemand(game, added, p);
                return sd[0] > 0 ? sd[1] / sd[0] : Double.POSITIVE_INFINITY;
            }
            case WATER: {
                // UtilitiesHandler: production = (built + base) x the utilities'
                // pooled fill, Σ fill x jobs / Σ jobs over both utilities' posts.
                UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
                long[] je = bm.getJobArrayPerCategory(BuildingType.ELECTRICITY);
                long[] jw = bm.getJobArrayPerCategory(BuildingType.WATER);
                double jobs = 0;
                for (int i = 0; i < je.length; i++) jobs += je[i] + jw[i];
                double filled = u.getAverageUtilityFill() * jobs;
                double base = u.getBaseWaterProduction();
                // ...and water's two parts, the fresh held to the city's limit (0.7.59). (Power's own figure is PW3's, THE FIRM SUPPLY.)
                double fresh = u.getFreshNameplate(), desal = u.getDesalNameplate();
                double demand = k * u.getWaterConsumption();
                for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
                    BuildingsTemplate t = e.getKey();
                    long n = e.getValue();
                    NewUnit nu = adding(game, t, n);
                    if (t.getCategory() == BuildingType.WATER) base += n * nu.getProduction1();
                    if (t.isFreshWater()) fresh += n * nu.getProduction1();
                    if (t.isSeaWater()) desal += n * nu.getProduction1();
                    demand += n * nu.getWaterConsumption();
                    if (t.getCategory() == BuildingType.ELECTRICITY || t.getCategory() == BuildingType.WATER) {
                        for (JobType job : JobType.values()) {
                            int posts = nu.getJobs(job);
                            if (posts == 0) continue;
                            jobs += (double) n * posts;
                            filled += (double) n * posts * (job.ordinal() < fill.length ? fill[job.ordinal()] : 1);
                        }
                    }
                }
                double average = jobs > 0 ? filled / jobs : 1;
                double supply = average == 0 ? 8000 : base * average;
                // Past the fresh water limit the fresh plants treat the cap (UtilitiesHandler,
                // THE FRESH WATER LIMIT); short of it, the line above to the bit.
                if (fresh * average > u.getFreshCap()) {
                    supply = UtilitiesHandler.waterOutput(fresh, desal, average, u.getFreshCap());
                }
                return supply > 0 ? demand / supply : Double.POSITIVE_INFINITY;
            }
            case ROADS:
                return roads(game, added, k).getUtilisation();
            case HIGHWAYS:
                return highwayFill(game, roads(game, added, k));
            case TRANSIT:
                return roads(game, added, k).getTransitCover();
            case CARE:
                return Health.coverageOf(carePlaces(game, m.care(), added, fill), k * m.care().populationServed(game.getCohorts()));
            case DEATH:
                return unburiedNext(game, added, k);
            case PLOTS: {
                Healthcare h = game.getHealthcare();
                double plots = bm.getCareCapacity(CareType.BURIAL);
                for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
                    if (e.getKey().getCare() == CareType.BURIAL) plots += e.getValue() * (double) e.getKey().getCapacity();
                }
                return h.monthsOfPlotsLeft(plots) / k;
            }
            case SCHOOL:
                return school(game, m.school(), added, p);
            case POLICE: {
                // Crime a head at the coverage the officers give the people there will be.
                Crime crime = game.getCrime();
                double pop = crime.getPopulation();
                if (!(pop > 0)) return 0;
                double c = Crime.coverageOf(officers(game, added), k * pop);
                return crime.crimesAt(c) * 12 * 100_000.0 / pop / Crime.CANADA_CRIMES_PER_100K;
            }
            case CELLS: {
                double caught = k * game.getCrime().getCaught();
                return Math.max(0, caught - cells(game, added) / Crime.SENTENCE_MONTHS);
            }
            case PORT:
                // Tonnes a month of the kind no berth takes, today's: a berth is judged on what crosses the boundary now (A BERTH THAT PAYS BACK).
                return portUncovered(game, m, added);
            default:
                return 0;
        }
    }

    /* ----- THE FIRM SUPPLY (PW3, 2026-11-23; spec-electricity 2.8) -----
       Power is planned for the year's peak hour (PowerDispatch.PLAN_PEAK_HOUR, losses in), and the supply that meets
       it is the FIRM supply: the jobless founding grid, the works' own power sold to the grid
       (PW1-P's first gap: it was left out, so a city of works was told to build plants it did not need), and every
       plant at its own posts' fill (the fifth: one farm's posts no longer lower every plant's output) times its
       credit - a fuelled row's production1 is its availability already and a station's its water, so 1, and a wind
       or solar farm its output in the peak hours at the city's place, falling as the variable rows make more of the
       city's energy (PowerDispatch.windCredit(), solarCredit()). The NEEDS YOU line is the reserve test's,
       CityNeeds.POWER_YELLOW. ----- */

    /** The variable rows' share of the city's energy with these added: the wind and solar farms' average kW (the sun's at the city's place) over the city's draw; 0 drawing nothing. */
    public static double variableShare(Game game, Map<BuildingsTemplate, Long> added) {
        UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
        double draw = u.getConsumption();
        if (!(draw > 0)) return 0;
        BuildingManager bm = game.getBuildingManager();
        double sun = PowerDispatch.solarYear(u.latitude(), u.trackers()), kw = 0;
        // ...the standing stacks, not the catalogue: a plant off the menu still stands and still generates.
        for (BuildingsStacks stack : bm.getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.isWind() || t.isSolar()) kw += stack.getQuantity() * t.getProduction1() * (t.isSolar() ? sun : 1);
        }
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            if (t.isWind() || t.isSolar()) kw += e.getValue() * adding(game, t, e.getValue()).getProduction1() * (t.isSolar() ? sun : 1);
        }
        return Math.max(0, kw) / draw;
    }

    /** A plant's firm kW over its production1 at this share of the city's energy from the variable rows: a wind farm's and a solar farm's credit at the city's place and rack (PowerDispatch), every other row's 1. */
    public static double credit(Game game, BuildingsTemplate t, double share) {
        if (t.isWind()) return PowerDispatch.windCredit(share);
        if (t.isSolar()) {
            UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
            return PowerDispatch.solarCredit(u.latitude(), u.trackers(), share);
        }
        return 1;
    }

    /** The share of a plant's production1 its staffing gives: its posts' fill, a hydro station's all of it with anybody on shift (its water, as UtilitiesHandler counts a station's month). */
    static double firmFill(BuildingsTemplate t, double staffed) {
        return t.isHydro() ? (staffed > 0 ? 1 : 0) : staffed;
    }

    /** Power's {firm supply, the peak hour's draw}, kW, with these buildings standing as well, at `p`'s demand: THE FIRM SUPPLY above. */
    private static double[] powerSupplyDemand(Game game, Map<BuildingsTemplate, Long> added, Ahead p) {
        UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
        BuildingManager bm = game.getBuildingManager();
        double[] fill = game.getPopulationManager().getJobFillRate();
        double demand = p.scale() * u.getConsumption() + WorksValue.pendingPowerLoad(game, p.months());
        double share = variableShare(game, added), firm = Math.max(0, u.getWorksPower());
        // ...each standing stack (not the catalogue's templates: a plant off the menu still stands), at its own posts' fill.
        for (BuildingsStacks stack : bm.getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            long q = stack.getQuantity();
            if (t.getCategory() == BuildingType.ELECTRICITY && q > 0)
                firm += q * t.getProduction1() * credit(game, t, share) * firmFill(t, staffing(t, fill));
        }
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            long n = e.getValue();
            NewUnit nu = adding(game, t, n);
            demand += n * nu.getElectricityConsumption();
            if (t.getCategory() == BuildingType.ELECTRICITY) firm += n * nu.getProduction1() * credit(game, t, share) * firmFill(t, staffing(nu, fill));
        }
        // PW9: the endowment owns no posts; hiring a small plant cannot remove its power. Match native dispatch.
        firm += u.getFoundingGridKw();
        return new double[] {Math.max(0, firm), demand * PowerDispatch.PLAN_PEAK_HOUR * p.powerFactor()};
    }

    /**
     * What the measure has against what it needs, with these buildings
     * standing as well, in the measure's own unit: generation or treatment
     * against the draw; the road's capacity against the trips on it, and
     * the room on transit's stock against the commuters (getUsableTransit(),
     * not its riders); the places, seats, officers
     * and cells against the people who need them (cells: six months of the
     * caught, a sentence each); the free plots and the ovens against the
     * dead waiting and dying. Burial plots, which are months and not a
     * stock against a need, read their plots left against two years of
     * burials.
     */
    public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Long> added) {
        return supplyDemand(game, m, added, NOW);
    }

    /** ...against the demand `p` says (0.7.51), as figure() reads it: the supply side unchanged. */
    public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        BuildingManager bm = game.getBuildingManager();
        double k = p.scale();
        switch (m.kind()) {
            case GRID: {
                UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
                double n = bm.getQuantity(Substations.ID);
                double draw = k * u.getConsumption() + WorksValue.pendingPowerLoad(game, p.months());
                for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
                    if (Substations.isSubstation(e.getKey())) n += e.getValue();
                    draw += e.getValue() * adding(game, e.getKey(), e.getValue()).getElectricityConsumption();
                }
                double supply = Math.max(u.getFoundingGridKw(), n * Substations.SUBSTATION_MW * 1000);
                double season = PowerDispatch.season(CityCalendar.monthOfYear(game.getMonth()));
                return new double[] {supply, Substations.peak(draw, season)};
            }
            case POWER:
                return powerSupplyDemand(game, added, p);
            case WATER: {
                double load = figure(game, m, added, p);
                UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
                double demand = k * u.getWaterConsumption();
                for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet())
                    demand += e.getValue() * adding(game, e.getKey(), e.getValue()).getWaterConsumption();
                return new double[] {load > 0 ? demand / load : 0, demand};
            }
            case ROADS: {
                // The streets' capacity against the streets' load; the whole road's with no highway standing.
                InfrastructureManager r = roads(game, added, k);
                return new double[] {r.getStreetCapacity(), r.getStreetLoad()};
            }
            case HIGHWAYS: {
                // The highways at free flow against the through trips (FR7).
                InfrastructureManager r = roads(game, added, k);
                return new double[] {r.getHighwayCapacity() * InfrastructureManager.FREE_FLOW, r.getThroughLoad()};
            }
            case TRANSIT: {
                InfrastructureManager r = roads(game, added, k);
                return new double[] {r.getUsableTransit(), r.getLoad(Traffic.COMMUTERS)};
            }
            case CARE:
                return new double[] {carePlaces(game, m.care(), added, fill), k * m.care().populationServed(game.getCohorts())};
            case DEATH: {
                Healthcare h = game.getHealthcare();
                double toHandle = k * h.getDeaths() + h.getUnburied();
                double waiting = figure(game, m, added, p);
                return new double[] {toHandle - waiting, toHandle};
            }
            case PLOTS: {
                Healthcare h = game.getHealthcare();
                double plots = bm.getCareCapacity(CareType.BURIAL);
                for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
                    if (e.getKey().getCare() == CareType.BURIAL) plots += e.getValue() * (double) e.getKey().getCapacity();
                }
                return new double[] {Healthcare.plotsRemaining(plots, h.getPlotsUsed()), k * h.getBurials() * CityNeeds.PLOTS_YELLOW};
            }
            case SCHOOL: {
                double[] places = bm.getStaffedEducationPlaces(fill);
                double seats = places[m.school().ordinal()];
                for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
                    if (e.getKey().getTeaches() == m.school()) seats += e.getValue() * (long) e.getKey().getCapacity() * staffing(adding(game, e.getKey(), e.getValue()), fill);
                }
                return new double[] {seats, schoolNeed(game, m.school(), p)};
            }
            case POLICE:
                return new double[] {officers(game, added),
                        k * game.getCrime().getPopulation() * Crime.FULL_OFFICERS_PER_100K / 100_000.0};
            case CELLS:
                return new double[] {cells(game, added), k * game.getCrime().getCaught() * Crime.SENTENCE_MONTHS};
            case PORT: {
                // The tonnes the berths take against the tonnes that could go by sea.
                double carriable = game.getPorts().carriable(m.cargo());
                return new double[] {carriable - portUncovered(game, m, added), carriable};
            }
            default:
                return new double[] {0, 0};
        }
    }

    /**
     * The people a school serves at `p`'s demand (0.7.51): a basic stage's
     * children or teens times p.scale(); above the ladder the students the
     * city would get and hire by then (CityNeeds.wanted(), its posts grown
     * by p.k()) with p's slack on top. At NOW it is today's: what NEEDS YOU
     * and the ring read.
     */
    static double schoolNeed(Game game, EducationType type, Ahead p) {
        if (type.isBasic()) {
            PopulationCohorts c = game.getCohorts();
            double children = c.get(AgeBand.CHILD);
            double need = type == EducationType.ELEMENTARY ? children * Education.ELEMENTARY_SHARE
                    : type == EducationType.MIDDLE ? children * (1 - Education.ELEMENTARY_SHARE) : c.get(AgeBand.TEEN);
            return p.scale() * need;
        }
        return CityNeeds.wanted(game, type, p.months(), p.k()) * (1 + p.slack());
    }

    /**
     * A kind of care's places with these buildings standing as well: the
     * staffed beds and the order's at the fill, less the people the fee
     * turned away last month (Healthcare.getPricedOut()) - the beds less the
     * priced out, as NEEDS YOU reads them (CityNeeds.careHave(); 0.7.106,
     * Jerus's A20), never under nothing. The priced out are held at the
     * month's count for an order and for the demand ahead: the model has no
     * forecast of who will be able to pay.
     */
    static double carePlaces(Game game, CareType care, Map<BuildingsTemplate, Long> added, double[] fill) {
        double cap = game.getBuildingManager().getStaffedCareCapacity(care, fill);
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            if (e.getKey().getCare() == care) cap += e.getValue() * (long) e.getKey().getCapacity() * staffing(adding(game, e.getKey(), e.getValue()), fill);
        }
        return Math.max(0, cap - game.getHealthcare().getPricedOut(care));
    }

    /** The figure as a share of what is needed, 0 to 1 - supply over demand, held to it - for a ring and the order bar's stacked bar. */
    public static double cover(Game game, Measure m, Map<BuildingsTemplate, Long> added) {
        double[] sd = supplyDemand(game, m, added);
        if (!(sd[1] > 0)) return 1;
        return Math.max(0, Math.min(1, sd[0] / sd[1]));
    }

    /*
     * SERVED (0.7.41): every gauge on the Build tab reads how much of its
     * need is met - supplyDemand()'s two halves, the one over the other,
     * unclamped (cover() is the same held to 0-1, for a ring's arc) - and is
     * judged by CityNeeds' one verdict. A gauge that is a supply against a
     * demand: power, water, the road, transit, care and the schools. The
     * dead, the plots, the police and the cells are not, and read as before.
     */

    /** Whether a measure's gauge reads as served (0.7.41): power, water, the road, transit, care and the schools. */
    public static boolean isServed(Measure m) {
        switch (m.kind()) {
            case POWER: case WATER: case ROADS: case HIGHWAYS: case TRANSIT: case CARE: case SCHOOL: case GRID: return true;
            default: return false;
        }
    }

    /** What the measure serves with these buildings standing as well (0.7.41): supply over demand, unclamped; NaN for a measure that is not served (isServed()). */
    public static double served(Game game, Measure m, Map<BuildingsTemplate, Long> added) {
        if (!isServed(m)) return Double.NaN;
        double[] sd = supplyDemand(game, m, added);
        return CityNeeds.servedShare(sd[0], sd[1]);
    }

    /**
     * ...and CityNeeds' one verdict on it (0.7.41): the lines of the NEEDS
     * YOU row that watches the measure. Transit has no line, and a school
     * above the ladder NEEDS YOU would not list has no row - fewer than a
     * class it would get and hire, or with no seats yet fewer than a first
     * school needs (CityNeeds.listsSchool(), 0.7.51): no verdict. Null for a
     * measure not served.
     */
    public static CityNeeds.Served verdict(Game game, Measure m, Map<BuildingsTemplate, Long> added) {
        if (!isServed(m)) return null;
        double share = served(game, m, added);
        switch (m.kind()) {
            case POWER:   return CityNeeds.verdict(CityNeeds.Kind.POWER, CareType.NONE, share);
            case WATER:   return CityNeeds.verdict(CityNeeds.Kind.WATER, CareType.NONE, share);
            case ROADS:   return CityNeeds.verdict(CityNeeds.Kind.ROADS, CareType.NONE, share);
            case HIGHWAYS: return CityNeeds.highwayVerdict(share, figure(game, m, added));
            case CARE:    return CityNeeds.verdict(CityNeeds.Kind.CARE, m.care(), share);
            case SCHOOL:
                if (m.school().isBasic()) return CityNeeds.verdict(CityNeeds.Kind.BASIC_SCHOOLS, CareType.NONE, share);
                double[] sd = supplyDemand(game, m, added);
                if (!CityNeeds.listsSchool(game, m.school(), sd[1], sd[0])) return CityNeeds.unjudged(share);
                return CityNeeds.verdict(CityNeeds.Kind.HIGHER_SCHOOL, CareType.NONE, share);
            default:      return CityNeeds.unjudged(share);
        }
    }

    /** A served measure's two lines in served terms (0.7.41), {off NEEDS YOU's list past this, red at or under this}; null for transit, for the highways (their verdict is the row's own level: CityNeeds.highwayVerdict()) and a measure not served. */
    public static double[] servedLines(Measure m) {
        switch (m.kind()) {
            case POWER:  return CityNeeds.servedLines(CityNeeds.Kind.POWER, CareType.NONE);
            case WATER:  return CityNeeds.servedLines(CityNeeds.Kind.WATER, CareType.NONE);
            case ROADS:  return CityNeeds.servedLines(CityNeeds.Kind.ROADS, CareType.NONE);
            case CARE:   return CityNeeds.servedLines(CityNeeds.Kind.CARE, m.care());
            case SCHOOL: return CityNeeds.servedLines(m.school().isBasic()
                    ? CityNeeds.Kind.BASIC_SCHOOLS : CityNeeds.Kind.HIGHER_SCHOOL, CareType.NONE);
            default:     return null;
        }
    }

    /** What a suggestion would leave its measure serving, and the verdict on it (0.7.41): what is on site and the order, standing - the served side of its after(); null for a measure not served. */
    public static CityNeeds.Served verdictAfter(Game game, Suggestion s) {
        return verdict(game, s.measure(), plus(onSite(game, s.measure()), added(game, s)));
    }

    /** What a suggestion changes on the city: its count of its building - and for a paving (0.7.70), as many gravel roads gone. */
    public static Map<BuildingsTemplate, Long> added(Game game, Suggestion s) {
        return s.paving() ? times(new LinkedHashMap<>(), pavingStep(game), s.count())
                : plus(new LinkedHashMap<>(), s.template(), s.count());
    }

    /** The road network with these buildings standing: InfrastructureManager.with(), the model's own curve. */
    static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Long> added) {
        return roads(game, added, 1);
    }

    /** ...with the city's own traffic times k (0.7.51): the load and every stream grown alike, then the buildings' own as before. */
    static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Long> added, double k) {
        InfrastructureManager now = game.getInfrastructureManager();
        double capacity = 0, highway = 0, transit = 0, load = (k - 1) * now.getLoad();
        // ...and the streets-and-highways split's inputs (FR7): the raw highway capacity added, the ground
        // the added buildings stand on, and the freight gates' load - grown with the trips, as the load is.
        double highwayRaw = 0, sqFt = 0, gates = (k - 1) * now.getGateLoad();
        double[] streams = new double[Traffic.values().length];
        for (Traffic s : Traffic.values()) streams[s.ordinal()] = (k - 1) * now.getLoad(s);
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            long n = e.getValue();
            if (t.getCategory() == BuildingType.INFRASTRUCTURE) {
                capacity += n * (double) t.getCapacity();
                highway += n * (long) t.getCapacity() * t.getFreightGrade();
                transit += n * t.getTransitCapacity();
                if (t.isHighway()) highwayRaw += n * (double) t.getCapacity();
            }
            if (t.getCategory() == BuildingType.RAIL || t.getCategory() == BuildingType.HAULAGE
                    || t.getCategory() == BuildingType.PORTS) gates += n * t.getRoadLoad();
            load += n * t.getRoadLoad();
            sqFt += n * t.getLandSqFt();
            for (Traffic s : Traffic.values()) streams[s.ordinal()] += n * t.loadOf(s);
        }
        return now.with(capacity, highway, transit, load, streams, highwayRaw, sqFt, gates);
    }

    /**
     * The trips a month one more highway carries at free flow (FR7): the
     * Elevated Highway's capacity at FREE_FLOW - the unit the through need
     * is read in, so a share of one is a share of a highway worth building.
     * 0 with no highway in the catalogue.
     */
    public static double highwayFlow(Game game) {
        for (BuildingsTemplate t : game.getBuildingManager().getKnownTemplates()) {
            if (t.getCategory() == BuildingType.INFRASTRUCTURE && t.isHighway()) {
                return t.getCapacity() * InfrastructureManager.FREE_FLOW;
            }
        }
        return 0;
    }

    /** The through need's figure (FR7): the through trips with no highway to take them, over what one more highway carries (CityNeeds.HIGHWAY_MIN_FILL of one bites); 0 with no highway to build. */
    static double highwayFill(Game game, InfrastructureManager r) {
        double one = highwayFlow(game);
        return one > 0 ? r.getThroughOverflow() / one : 0;
    }

    /**
     * Trips over the line: what the road carries past STRAINED of its
     * capacity, the line NEEDS YOU lists the roads at. What a road or a line
     * relieves is how much of this one building takes away - more capacity,
     * a grade-separated share for freight, riders off the road - which is
     * how roads and transit are compared at all when capacity has its own
     * streams.
     */
    static double roadsOver(Game game, Map<BuildingsTemplate, Long> added) {
        InfrastructureManager r = roads(game, added);
        // The streets' (FR7): their load past STRAINED of their capacity - the road's own with no highway standing.
        return r.getStreetLoad() - InfrastructureManager.STRAINED * r.getStreetCapacity();
    }

    /** The dead next month at today's deaths: those waiting and those dying, less what the free plots and the ovens take (Healthcare.settleDeaths()'s arithmetic). */
    static double unburiedNext(Game game, Map<BuildingsTemplate, Long> added) {
        return unburiedNext(game, added, 1);
    }

    /** ...with k times today's deaths (0.7.51): the unburied are a stock, and stay as they are. */
    static double unburiedNext(Game game, Map<BuildingsTemplate, Long> added, double k) {
        Healthcare h = game.getHealthcare();
        BuildingManager bm = game.getBuildingManager();
        double[] fill = game.getPopulationManager().getJobFillRate();
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, fill);
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            if (t.getCare() == CareType.BURIAL) plots += e.getValue() * (double) t.getCapacity();
            if (t.getCare() == CareType.CREMATION) ovens += e.getValue() * (long) t.getCapacity() * staffing(adding(game, t, e.getValue()), fill);
        }
        double free = Healthcare.plotsRemaining(plots, h.getPlotsUsed());
        double deaths = k * h.getDeaths();
        double waiting = Math.max(0, deaths + h.getUnburied() - free - Math.max(0, ovens));
        return Math.min(waiting, deaths * Healthcare.MAX_BACKLOG_MONTHS);
    }

    /** A school's figure: a basic stage's coverage (Education's own cover, places over the children it serves), or above the ladder who would come and be hired over the seats (0.7.51; who would come until then). */
    static double school(Game game, EducationType type, Map<BuildingsTemplate, Long> added) {
        return school(game, type, added, NOW);
    }

    /** ...against the people `p` says it serves (schoolNeed()). */
    static double school(Game game, EducationType type, Map<BuildingsTemplate, Long> added, Ahead p) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double[] places = game.getBuildingManager().getStaffedEducationPlaces(fill);
        double seats = places[type.ordinal()];
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            if (e.getKey().getTeaches() == type) seats += e.getValue() * (long) e.getKey().getCapacity() * staffing(adding(game, e.getKey(), e.getValue()), fill);
        }
        double need = schoolNeed(game, type, p);
        if (type.isBasic()) {
            if (need <= 0) return seats > 0 ? 1 : 0;
            return Math.max(0, Math.min(1, seats / need));
        }
        return need / Math.max(seats, 1);
    }

    static double officers(Game game, Map<BuildingsTemplate, Long> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double n = game.getBuildingManager().getStaffedSafetyCapacity(SafetyType.POLICE, fill);
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            if (e.getKey().getSafety() == SafetyType.POLICE) n += e.getValue() * (long) e.getKey().getCapacity() * staffing(adding(game, e.getKey(), e.getValue()), fill);
        }
        return n;
    }

    static double cells(Game game, Map<BuildingsTemplate, Long> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double n = game.getBuildingManager().getStaffedSafetyCapacity(SafetyType.PRISON, fill);
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            if (e.getKey().getSafety() == SafetyType.PRISON) n += e.getValue() * (long) e.getKey().getCapacity() * staffing(adding(game, e.getKey(), e.getValue()), fill);
        }
        return n;
    }

    /**
     * Whether a figure is off NEEDS YOU's list: past the line it is listed
     * at (the yellow line, strictly), which way past depending on the row.
     * The unburied and the caught-not-held are judged as their rows are:
     * the dead are listed while anybody waits, the cells from one person.
     */
    public static boolean clear(Measure m, double figure) {
        switch (m.kind()) {
            case GRID: return figure <= 1;
            case POWER: return figure < CityNeeds.POWER_YELLOW;
            case WATER: return figure < CityNeeds.NETWORK_YELLOW;
            case ROADS:   return figure < InfrastructureManager.STRAINED;
            case HIGHWAYS: return figure < CityNeeds.HIGHWAY_MIN_FILL;
            case CARE:    return figure > (m.care() == CareType.GENERAL ? CityNeeds.GENERAL_YELLOW : CityNeeds.OTHER_CARE_YELLOW);
            case DEATH:   return !(figure > 0);
            case PLOTS:   return figure > CityNeeds.PLOTS_YELLOW;
            case SCHOOL:  return m.school().isBasic() ? figure > CityNeeds.SCHOOLS_YELLOW : figure < CityNeeds.SEATS_YELLOW;
            case POLICE:  return figure < CityNeeds.CRIME_YELLOW;
            case CELLS:   return figure < CityNeeds.CELLS_YELLOW;
            case PORT:    return !(figure > 0);        // no tonne left uncovered; whether a berth PAYS BACK is portTerminal()'s
            default:      return true;
        }
    }

    /* =====================================================================
       THE SCARCE STAFF, AND WHAT AN ORDER NEEDS
       ===================================================================== */

    /** The job type among a building's posts with the city's lowest fill rate, or null for a building with no posts. */
    public static JobType scarceJob(BuildingsTemplate t, double[] fill) {
        JobType worst = null;
        double at = Double.MAX_VALUE;
        for (JobType job : JobType.values()) {
            if (t.getJobs(job) <= 0) continue;
            double f = fill != null && job.ordinal() < fill.length ? fill[job.ordinal()] : 1;
            if (f < at) { at = f; worst = job; }
        }
        return worst;
    }

    /** Posts and land an order needs: posts by job type (index JobType.ordinal()), then the land in the last slot. */
    public static double[] needs(Map<BuildingsTemplate, Long> order) {
        return needs(order, (t, n) -> NewUnit.standing(t));
    }

    /** ...of an order as it would open now (QN1): each building to add (a count above nothing) as one new building of its template, a removal as it stands. */
    public static double[] needs(Game game, Map<BuildingsTemplate, Long> order) {
        return needs(order, (t, n) -> adding(game, t, n));
    }

    private static double[] needs(Map<BuildingsTemplate, Long> order,
                                  java.util.function.BiFunction<BuildingsTemplate, Long, NewUnit> read) {
        double[] out = new double[JobType.values().length + 1];
        for (Map.Entry<BuildingsTemplate, Long> e : order.entrySet()) {
            NewUnit u = read.apply(e.getKey(), e.getValue());
            for (JobType job : JobType.values()) out[job.ordinal()] += (double) e.getValue() * u.getJobs(job);
            out[out.length - 1] += e.getValue() * e.getKey().getLandSqFt();
        }
        return out;
    }

    /**
     * What an order of several buildings costs all in (0.7.34): each one's
     * Game.quoteBuild() total, as its own card quotes it and its own Build
     * charges it - the order bar's "$X all in" and its Build button's label,
     * which the screen added up itself until now. Reads; changes nothing.
     */
    public static double quoteTotal(Game game, Map<BuildingsTemplate, Long> order) {
        double total = 0;
        for (Map.Entry<BuildingsTemplate, Long> e : order.entrySet()) total += game.quoteBuild(e.getKey(), e.getValue()).total;
        return total;
    }

    /**
     * ...and of the overview's suggestions (0.7.38): each one's own quote,
     * its price(), which is Game.quoteBuild() as its card shows it and its
     * Build charges it, added in their order. WHAT WOULD HELP MOST's "all
     * three ≈ $X" read it until 0.7.51; it reads the run's invoice now
     * (Game.buildRunInvoice() of run()), what placing them in turn charges.
     * Reads; changes nothing.
     */
    public static double quoteTotal(List<Suggestion> advice) {
        double total = 0;
        for (Suggestion s : advice) total += s.price();
        return total;
    }

    /* =====================================================================
       THE SUGGESTIONS

       AHEAD OF THE NEED, ON ITS OWN GROUND (0.7.51). Jerus, of "Build all
       three": "the building ideas is flawed, it doesnt take into account
       land price, and it doesnt build any slack ... make it so that alot of
       its ideas also take into account the same as businesses do, aka a
       projection". The count was the least that took a row off NEEDS YOU
       today, and no more: forty clinics left general care at 81%, and back
       on the list the month they opened. The price a unit never saw the
       ground, so a wind farm's 1.74M sq ft for 8,100 kW beat a coal plant's
       2M for 280,000. Now an order is sized the way the businesses size a
       plant (BusinessInvestment.planMaker()): to the demand when it opens
       plus HORIZON months, grown by their growthFactor(), with SLACK on
       top, and a served gauge has to reach 100% of that (ahead()). Each
       building is priced with its ground at what the land office charges
       (landValue()), against the ground the cards before it leave, so the
       cards and "Build all three" agree on where the run stops. A school
       above the ladder is wanted only for the students the city would get
       and hire (CityNeeds.wanted(), its feeder's graduates by then and the
       posts their degree fills), where it was everyone who would come: a
       founded city of 118 was told to build a $417M university for 33. And
       the cash no longer cuts a count: it was cut to what the cash the
       cards before left could pay, while a card after it borrowed anyway.
       A card is the count that keeps its need ahead, on credit when that
       cash is short of its quote, by how much (credit).
       ===================================================================== */

    /** At most this many suggestions, one per need. */
    public static final int MAX_SUGGESTIONS = 3;

    /** The most of one building a search will count to. */
    static final int MOST = 1 << 22;

    /** The businesses' headroom, retained for explicit callers. Normal cards use AutoBuilder.getSlack() (HW1). */
    public static final double SLACK = BusinessInvestment.TARGET_HEADROOM;

    /** The least months past an order's opening it is sized for: the businesses' (BusinessInvestment.PLANNING_HORIZON); an order that opens later than that is sized further past it, as the businesses size theirs (BusinessInvestment.horizonFor(), since BA1). */
    public static final double HORIZON = BusinessInvestment.PLANNING_HORIZON;

    /**
     * The demand an order is sized against (0.7.51): today's, `months`
     * ahead grown by k (BusinessInvestment.growthFactor()), and padded by
     * `slack`. Power's physical dispatch month also carries its seasonal peak
     * factor; other callers retain the annual peak-hour demand.
     */
    public record Ahead(double months, double k, double slack, double powerFactor) {
        /** Existing callers plan the annual peak hour; a physical dispatch month supplies its own seasonal peak factor. */
        public Ahead(double months, double k, double slack) { this(months, k, slack, 1); }
        /** What today's demand is multiplied by. */
        public double scale() { return k * (1 + slack); }
    }

    /** Today's demand, no slack: what NEEDS YOU reads. */
    public static final Ahead NOW = new Ahead(0, 1, 0);

    /**
     * The demand an order that opens after `lead` months is sized to: the
     * wait (a stalled one, NaN, counts as none), plus the businesses' horizon
     * for a building that takes so long (BusinessInvestment.horizonFor(): HORIZON
     * for a short wait, growing with a long one - the wait was held to twelve
     * months here until BA1) - the businesses' growthFactor() over those
     * months, padded by the player's automatic reserve setting.
     */
    public static Ahead opening(Game game, double lead) {
        return opening(game, lead, game.getAutoBuilder().getSlack());
    }

    /** ...with another slack past it (0.7.73): automatic building's, the player's slider (AutoBuilder). */
    public static Ahead opening(Game game, double lead, double slack) {
        double wait = Double.isNaN(lead) ? 0 : Math.max(0, lead);
        double months = wait + BusinessInvestment.horizonFor(wait);
        return new Ahead(months, game.getBusinessInvestment().growthFactor(months), slack);
    }

    /**
     * Whether these buildings keep the measure ahead at `p`'s demand: off
     * NEEDS YOU's list there (clear()), and a served measure - not transit,
     * which has no line - at 100% of it or more. With SLACK in p that is
     * green, not just off the list.
     *
     * THE HIGHWAYS (FR7) ARE HELD TO THEIR LINE AND NOT TO 100%: a highway is
     * not built to stand idle (CityNeeds.HIGHWAY_MIN_FILL), so the through
     * trips with no highway to take them are the streets' to carry until they
     * would fill six tenths of one - the line the measure is off NEEDS YOU's
     * list at, and the line an order is sized to. Held to every through trip
     * on a highway, a city with the smallest through traffic would be told to
     * build one.
     */
    public static boolean ahead(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p) {
        // A berth (FR3): ahead while no terminal of its kind pays back, on today's tonnes - no projection, no slack - or the city owns no coast.
        if (m.kind() == Kind.PORT) return portTerminal(game, m, added, java.util.Collections.emptySet()) == null;
        if (!clear(m, figure(game, m, added, p))) return false;
        if (!isServed(m) || m.kind() == Kind.TRANSIT || m.kind() == Kind.HIGHWAYS) return true;
        double[] sd = supplyDemand(game, m, added, p);
        return CityNeeds.servedShare(sd[0], sd[1]) >= 1;
    }

    /** Automatic works (HW1): every essential service meets the whole demand, including highways, police and professional schools. */
    public static boolean maintained(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p) {
        if (!AutoBuilder.essential(m)) return ahead(game, m, added, p);
        if (!warranted(game, m)) return true;
        double[] sd = supplyDemand(game, m, added, p);
        return !(sd[1] > 0) || sd[0] >= sd[1];
    }

    /**
     * What the city lacks this month (PW3, 2026-11-24): every measure but power is lacking when it is not maintained now; power is short when
     * the firm supply is below the draw of the peak block in the year's highest month (PowerDispatch.PLAN_PEAK_SEASON), the block the month's
     * dispatch serves and charges at its worst: the peak hour (PLAN_PEAK_HOUR) is the plan's, not the month's, and the peak block at the
     * year's mean (PLAN_PEAK) leaves the highest month's unserved.
     */
    public static boolean lacksNow(Game game, Measure m) {
        if (m.kind() != Kind.POWER) return !maintained(game, m, Map.of(), NOW);
        if (!warranted(game, m)) return false;
        double[] sd = supplyDemand(game, m, Map.of(), NOW);
        return sd[1] > 0 && sd[0] < sd[1] * PowerDispatch.PLAN_PEAK_SEASON / PowerDispatch.PLAN_PEAK_HOUR;
    }

    /** Jerus's HW1 first-work threshold: 150 students for a 500-seat college, and the same share for other oversized works. */
    public static final double FIRST_WORK_SHARE = .30;

    /** Below a useful share of the smallest work, no shortage is declared or forced (HW1). */
    public static boolean warranted(Game game, Measure m) {
        if (m.kind() == Kind.GRID) return supplyDemand(game, m, Map.of())[1] > 0;
        if (!AutoBuilder.essential(m)) return true;
        double demand = supplyDemand(game, m, Map.of())[1];
        if (!(demand > 0)) return false;
        double least = Double.POSITIVE_INFINITY;
        for (BuildingsTemplate t : game.getBuildingManager().getKnownTemplates()) {
            if (!m.serves(t)) continue;
            double unit = built(game, m, t);
            if (m.kind() == Kind.HIGHWAYS) unit *= InfrastructureManager.FREE_FLOW;
            if (unit > 0) least = Math.min(least, unit);
        }
        return !Double.isFinite(least) || demand >= FIRST_WORK_SHARE * least;
    }

    /**
     * What `sqFt` of ground is worth to the city with `landLeft` free: the
     * free part at the cheaper of what a business pays the city for it and
     * the land office's price, the rest at the land office's
     * (LandManager.getOfficePricePerSqFt()) - what the city would pay to
     * replace a square foot it builds on.
     */
    public static double landValue(Game game, double sqFt, double landLeft) {
        LandManager land = game.getLandManager();
        double office = land.getOfficePricePerSqFt();
        double free = Math.min(sqFt, Math.max(0, landLeft));
        return free * Math.min(land.getPricePerSqFt(), office) + Math.max(0, sqFt - Math.max(0, landLeft)) * office;
    }

    /**
     * ...a template's ground (RG1), `landLeft` the ground it may take as
     * availableGround() reads it: a Wind or Solar Farm in the farmland reserve
     * while one stands - as much as the reserve holds at the reserve's price
     * (LandManager.groundPriceFor(), the farmland price as far as the going
     * price: what the city gives that ground up at, since it may go to
     * nothing but a farm), the rest at landValue() on what is left past the
     * reserve's. Every other template, and a farm with no reserve, is
     * landValue() itself.
     */
    public static double landValue(Game game, BuildingsTemplate t, double sqFt, double landLeft) {
        LandManager land = game.getLandManager();
        String reserve = land.reserveKeyOf(t);
        if (reserve == null) return landValue(game, sqFt, landLeft);
        double held = land.heldBy(reserve), in = Math.min(Math.max(0, sqFt), held);
        return in * land.groundPriceFor(reserve) + landValue(game, sqFt - in, landLeft - held);
    }

    /** The farmland reserve's held ground a template may take besides the ground free for anyone (RG1): a Wind or Solar Farm's while a reserve stands, else none. */
    static double reserveHeldFor(Game game, BuildingsTemplate t) {
        LandManager land = game.getLandManager();
        String reserve = land.reserveKeyOf(t);
        return reserve == null ? 0 : land.heldBy(reserve);
    }

    /** Ground remaining for this order's actual land use after earlier cards' footprints - and for a Wind or Solar Farm the farmland reserve's held ground while one stands (RG1, reserveHeldFor()). */
    static double availableGround(Game game, BuildingsTemplate t, double buildingGroundLeft) {
        LandManager land = game.getLandManager();
        return Math.max(0, buildingGroundLeft
                + (land.freeForAnyone(Game.groundUse(t)) - land.getAvailableSqFt()) + reserveHeldFor(game, t));
    }

    /** Why the next plan lost: greater whole-life net cost, or it cannot close the need (HW1). NO_ROOM remains for historical callers. */
    public enum Lost { DEARER, CANNOT_CLOSE, NO_ROOM }

    /**
     * One suggested order (0.7.51: one count, the one that keeps the need
     * ahead - the cash no longer caps it).
     *
     * @param count       what to order: the least that keeps the need ahead
     *                    at its projection, after what is on site
     * @param price       its quote - Game.quoteBuild(), what Build charges;
     *                    the city pays nothing for its own ground
     * @param before      the need's figure now (NEEDS YOU's)
     * @param whenOnSite  the figure once what is on site opens
     * @param after       ...and with this order too, at today's demand
     * @param afterAtOpening ...at the binding planning date's demand, projected
     *                    (ahead's months and k) without the slack
     * @param closes      false when no count of this building keeps the need
     *                    ahead (crime with every officer it can use, transit
     *                    past the share of commuters who will ride): then
     *                    count is where it stops helping
     * @param needsCredit its quote is more than the cash the suggestions
     *                    before it leave
     * @param onSite      units on site that already serve the measure
     * @param unit        what one serves at today's staffing
     * @param pricePerUnit informational net cost per physical unit relieved;
     *                    since HW1 the whole plan's value ranks it instead
     * @param ahead       the demand it is sized to (power's binding date from completion through opening(lead), otherwise opening(lead))
     * @param lead        the city order's own wait, Game.quoteBuild()'s months
     *                    for the count at today's demand with the slack
     * @param landSqFt    the ground the count stands on
     * @param landValue   ...at landValue(), on the land the ones before leave
     * @param landShort   square feet past the land the ones before leave, or 0
     * @param runnerUp    the next building in the ranking, or null for none
     * @param runnerUpPer ...its figure, the same (lifePerServed()), NaN for none
     * @param runnerUpLost ...and why it lost, null for none
     * @param credit      the part of its quote the cash the ones before leave
     *                    does not cover: 0 unless needsCredit
     * @param paving      (0.7.70) count gravel roads paved, not new buildings:
     *                    template is the Paved Road they become, price
     *                    Game.quotePave(), landSqFt the ground it frees as a
     *                    negative and landValue that ground's worth taken
     *                    off (ConstructionControl, F)
     */
    public record Suggestion(CityNeeds.Need need, Measure measure, BuildingsTemplate template,
                             long count, double price, double before, double whenOnSite, double after,
                             double afterAtOpening, boolean closes, boolean needsCredit, long onSite,
                             double unit, double pricePerUnit, Ahead ahead, double lead,
                             double landSqFt, double landValue, double landShort,
                             BuildingsTemplate runnerUp, double runnerUpPer, Lost runnerUpLost, double credit,
                             boolean paving, WorksValue.Value value, WorksValue.Value runnerUpValue) {
        /** The complete plan's comparable net present value, separate from its informational unit cost. */
        public double planValue() { return value == null ? price : value.net(); }
    }

    /** The rule, on this city now. */
    public static List<Suggestion> suggest(Game game) {
        return suggest(game, CityNeeds.measure(game, CityNeeds.PLAIN));
    }

    /**
     * The rule, on NEEDS YOU as already measured (the screen measures it
     * once a redraw): each card on the cash and the ground the ones before
     * it leave.
     */
    public static List<Suggestion> suggest(Game game, List<CityNeeds.Need> all) {
        List<Suggestion> out = new ArrayList<>();
        double cashLeft = game.getCash();
        double landLeft = game.getLandManager().getAvailableSqFt();
        for (Measure m : AutoBuilder.remit(game, all)) {
            if (out.size() >= MAX_SUGGESTIONS) break;
            CityNeeds.Need need = needFor(all, m);
            if (need == null) continue;
            Suggestion s = suggestFor(game, need, m, cashLeft, landLeft);
            if (s == null) continue;
            out.add(s);
            cashLeft = Math.max(0, cashLeft - s.price());
            landLeft = Math.max(0, landLeft - s.landSqFt());
        }
        return out;
    }

    /** The suggestions as one run, as "Build all three" places it: each card's building and count in their order, two of one building added together - and no paving (0.7.70), which is not a build order and has its card's own button. */
    public static LinkedHashMap<BuildingsTemplate, Long> run(List<Suggestion> advice) {
        LinkedHashMap<BuildingsTemplate, Long> run = new LinkedHashMap<>();
        for (Suggestion s : advice) if (!s.paving()) run.merge(s.template(), (long) s.count(), Long::sum);
        return run;
    }

    /** The suggestions "Build all three" places (0.7.70): every one but a paving. */
    public static List<Suggestion> builds(List<Suggestion> advice) {
        List<Suggestion> out = new ArrayList<>();
        for (Suggestion s : advice) if (!s.paving()) out.add(s);
        return out;
    }

    /** One building weighed for a need: its count at its projection, and where it ranks - or, paving, gravel roads paved (0.7.70). */
    private record Weighed(BuildingsTemplate t, int tier, double per, double unit, long count,
                           boolean closes, boolean fits, Ahead ahead, double lead, boolean paving, WorksValue.Value value,
                           double rank) { }

    /** One need's suggestion, or null when what is on site already keeps it ahead or no building can help. */
    static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft) {
        return suggestFor(game, need, m, cashLeft, landLeft, game.getAutoBuilder().getSlack());
    }

    /**
     * ...sized with `slack` past its projection in place of SLACK (0.7.73):
     * automatic building's spare margin, the player's slider (AutoBuilder) -
     * the same ranking, the same projection, the same count at another
     * slack. `need` may be null for a measure NEEDS YOU has no row for (a
     * basic stage that is not the bottleneck): its figure before is then the
     * measure's own as it stands.
     */
    public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft,
                                        double slack) {
        return suggestFor(game, need, m, cashLeft, landLeft, slack, java.util.Collections.emptySet());
    }

    /** ...with the buildings in `skip` left out of the ranking (0.7.73): automatic building's, for one that would not pay back (since 0.7.104; one its budget could not run from 0.7.73), and since 0.7.101 one the money or the ground cannot pay for one of - the next in the ranking is the card; and since 0.7.103 the test player's, every building but the roads (LongPlaytest.roadsByNeed()). */
    public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft,
                                        double slack, java.util.Set<BuildingsTemplate> skip) {
        return suggestFor(game, need, m, cashLeft, landLeft, slack, skip, false);
    }

    /** ...and with the city's gravel roads paved left out too when `noPaving` (0.7.101): a paving the money cannot pay for, passed over the same way (and the test player's paving once offered, since 0.7.103). */
    public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft,
                                        double slack, java.util.Set<BuildingsTemplate> skip, boolean noPaving) {
        return suggestFor(game, need, m, cashLeft, landLeft, slack, skip, noPaving, true);
    }

    /**
     * The shared Build-card and automatic full-service target, with the player's reserve setting (HW1). The reading changes nothing in
     * the city, so the new units of the buildings under way are fitted once in it and held to its end (R1g; NewUnit.heldWhile()).
     */
    public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft,
                                        double slack, java.util.Set<BuildingsTemplate> skip, boolean noPaving, boolean maintain) {
        if (m.kind() != Kind.POWER)
            return NewUnit.heldWhile(game, () -> suggestionFor(game, need, m, cashLeft, landLeft, slack, skip, noPaving, maintain));
        try (WorksValue.Scope held = WorksValue.hold(game)) {
            return NewUnit.heldWhile(game, () -> suggestionFor(game, need, m, cashLeft, landLeft, slack, skip, noPaving, maintain));
        }
    }

    /** suggestFor()'s reading itself. */
    private static Suggestion suggestionFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft,
                                            double slack, java.util.Set<BuildingsTemplate> skip, boolean noPaving, boolean maintain) {
        if (AutoBuilder.essential(m) && !warranted(game, m)) return null;
        // A berth (FR3): the terminal that pays back, if any (A BERTH THAT PAYS BACK, below) - not ranked like a service.
        if (m.kind() == Kind.PORT) return portSuggestion(game, m, cashLeft, landLeft, skip);
        Map<BuildingsTemplate, Long> site = maintain ? game.getAutoBuilder().pendingCapacity(game, m) : onSite(game, m);
        double whenOnSite = figure(game, m, site);
        // JUDGEMENT: what is on site already keeps it ahead, sized as an order placed now would be - nothing to add.
        // Power is tested over each candidate's commissioning/load frontier below, with both sides at the same date;
        // a saved first-unit promise never opens the whole site. Power passed is still weighed by the spread.
        boolean full = maintain && AutoBuilder.essential(m), power = m.kind() == Kind.POWER;
        double before = need != null ? need.value() : figure(game, m, new LinkedHashMap<>());
        Ahead now = opening(game, 0, slack);
        WorksValue.PowerDates dates = power ? new WorksValue.PowerDates(game, site) : null;
        if (!full && !power && ahead(game, m, site, now))
            return null;

        List<Weighed> weighed = new ArrayList<>();
        // What each power candidate was read with (the plants at its binding dated requirement, and its actual lead), for placeFarms().
        Map<BuildingsTemplate, Map<BuildingsTemplate, Long>> opened = new LinkedHashMap<>();
        Map<BuildingsTemplate, Double> leads = new LinkedHashMap<>();
        // ...and every firm row the city could order, the faster ones each due row is weighed against (THE NEED DATE, below), each with
        // the months until the menu brings it (0 for a row known now).
        Map<BuildingsTemplate, Integer> firmRows = new LinkedHashMap<>();
        // ...and the rows due by the waiver alone whose need date is already past their last on-time month (late, below).
        java.util.Set<BuildingsTemplate> late = new java.util.HashSet<>();
        for (BuildingsTemplate t : game.getBuildingManager().getKnownTemplates()) {
            // The streets' candidates: every road and line but a highway, which gives no access (FR7); the highways' the Elevated Highway alone.
            boolean candidate = m.kind() == Kind.ROADS
                    ? t.getCategory() == BuildingType.INFRASTRUCTURE && !t.isHighway() : m.serves(t);
            if (!candidate || skip.contains(t)) continue;
            // PW3 (REV-PW3 #3): the reserve test credits a wind or solar farm at its falling credit (THE FIRM SUPPLY), as planners credit
            // them at their ELCC, and holds its count to the farms that keep the variable rows' share of the energy at or under
            // PowerDispatch.CREDIT_HALF (reserveMost()): past it a fleet adds only the floor's tenth, so wind alone never passes the test.
            // The firm rows are ranked by the screening curve and a farm that closes the gap is weighed against that pick (placeFarms()).
            double unit = unit(game, m, t);
            if (maintain && roadKind(m) && unit <= 0) unit = built(game, m, t);
            // A building the city cannot staff at all, or one that does not
            // move the figure, serves nothing.
            if (!(unit > 0)) continue;
            if (power && !isFarm(t)) firmRows.put(t, 0);
            // Counted twice: at today's demand with the slack, for the order's
            // own wait; then at the demand it opens to after that wait.
            long most = MOST;
            Map<BuildingsTemplate, Long> one = plus(new LinkedHashMap<>(), t, 1);
            long n;
            double lead;
            Ahead p;
            long[] found;
            Map<BuildingsTemplate, Long> at = site;
            if (power) {
                PowerPlan plan = powerPlan(game, m, t, dates, slack, maintain);
                if (plan == null) continue;
                n = plan.count();
                lead = plan.lead();
                at = plan.binding().at();
                p = plan.binding().ahead();
                found = new long[] {n, plan.closes() ? 1 : 0};
            } else {
                long[] today = count(game, m, site, one, new Ahead(0, 1, slack), most, maintain);
                if (today[0] <= 0 && !full) continue;
                n = Math.max(1, today[0]);
                while (true) {
                    lead = game.quoteBuild(t, n).months;
                    p = opening(game, lead, slack);
                    found = count(game, m, at, one, p, most, maintain);
                    if (!full || found[0] <= n) break;
                    n = found[0];
                }
            }
            // ...and a plant whose whole dated requirement the site already covers is not a reserve order (the spread may still weigh it).
            if (power && (full ? maintained(game, m, at, p) : ahead(game, m, at, p))) continue;
            if (found[0] <= 0) {
                if (!full || found[1] != 1) continue;
                found = new long[] {1, 1};
            }
            // PW3, the row's own test (2026-11-24, REV-PW3c to REV-PW3e): a gap at its opening under FIRST_WORK_SHARE (Jerus's HW1 share) of
            // what the order adds is a need only when the seasonal peak (PowerDispatch.PLAN_PEAK_SEASON: the peak block of the year's highest
            // month, what the month's dispatch serves at its worst) is short at the row's own completion: its lead and the town's growth over it,
            // no horizon and no slack. A gap only the opening's horizon and slack project waits for the share (the ensemble: a 67,410 kW plant
            // for a 29 kW gap 60 months out, $8.1M borrowed). A row that passes is due: the need date falls at or before its completion, so this
            // month is its last on time, or it is late: due by the waiver alone and short a month before its completion too. Which due row
            // orders is decided over them all after the loop (THE NEED DATE).
            if (power) {
                double wait = Double.isNaN(lead) ? 0 : Math.ceil(Math.max(0, lead));
                double[] sd = supplyDemand(game, m, at, p);
                boolean share = sd[1] - sd[0] >= FIRST_WORK_SHARE * found[0] * unit;
                if (!share && !seasonalShort(game, m, dates.openBy(wait), wait)) continue;
                double prior = Math.max(0, wait - 1);
                if (!share && seasonalShort(game, m, dates.openBy(prior), prior)) late.add(t);
            }
            boolean closes = found[1] == 1;
            // PW3 (REV-PW3 #3): a firm row (all but wind and solar) is ranked by the screening curve; a wind or solar farm by its net cost
            // (reserveRank()): the total for one that closes the gap, which placeFarms() weighs against the firm pick, and a firm kW added
            // for one that does not. A farm that adds no firm kW is no reserve.
            boolean farm = power && (t.isWind() || t.isSolar());
            double rank = !power ? 0 : farm ? reserveRank(game, t, found[0], closes, lead, at, p, landLeft)
                    : screening(game, t, found[0], at, p, landLeft);
            if (farm && !(rank < Double.POSITIVE_INFINITY)) continue;
            if (power) {
                opened.put(t, at);
                leads.put(t, lead);
            }
            boolean fits = game.landNeededFor(t, found[0]) <= availableGround(game, t, landLeft);
            // Every candidate's figure since 0.7.101: its order over its life
            // for each unit it will serve - see A BUILDING OVER ITS LIFE. (A
            // road's was its cost over its life a trip from 0.7.70, a living
            // care building's its order over the places the need lacks from
            // 0.7.71, and the rest their price a unit with their ground.)
            WorksValue.Value value = WorksValue.of(game, m, onSite(game, m), t, found[0], landLeft, cashLeft);
            double per = value.perUsed();
            // Automatic building can buy land: its full price ranks it, rather than a free-land veto (HW1).
            // PW3: power's reserve order is ranked by the screening curve among the firm rows, and a wind or solar farm by its net cost
            // (above), within HW1's tier; every other measure by the plan's present value, as HW1 ranks it.
            int tier = closes ? 0 : 1;
            weighed.add(new Weighed(t, tier, per, unit, found[0], closes, fits, p,
                    power ? lead : game.quoteBuild(t, found[0]).months,
                    false, value, power ? rank : value.net()));
        }
        // PW3, THE NEED DATE (REV-PW3f's P1, 2026-11-24): a due row orders only when no faster firm row, one that can still be on line by
        // the date, meets the need at its opening for less (timely()). Each row tested at its own lead, the slowest and biggest was the one
        // that passed: the Oil-Fired Plant's 102.8-month lead saw the seasonal gap of m205 that the 30-month Diesel Station could still
        // meet, and LongPlaytest borrowed $13.7M at m102; before that, an 86,181 kW plant for towns drawing 3,000 kW (REV-PW3d) and, with the
        // share held until the month was short, every diesel a whole lead late (REV-PW3e).
        // A firm row the calendar brings onto the menu within a due row's lead is weighed too, by its arrival and its own lead: a 1908
        // town short in 1917 does not commit to the 1900 Oil-Fired Plant while the 1910 Diesel Station can still meet the date.
        if (power && !weighed.isEmpty()) {
            double longest = 0;
            for (Weighed w : weighed) longest = Math.max(longest, w.lead());
            for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
                if (!m.serves(t) || isFarm(t) || skip.contains(t) || firmRows.containsKey(t) || game.getBuildingManager().isTemplateKnown(t))
                    continue;
                for (int k = 1; k < longest; k++)
                    if (game.getBuildingManager().isTemplateKnownIn(t, k)) {
                        if (unit(game, m, t) > 0) firmRows.put(t, k);
                        break;
                    }
            }
        }
        if (power) {
            List<Weighed> due = new ArrayList<>(weighed);
            weighed.clear();
            for (Weighed w : due)
                if (timely(game, m, w, opened.get(w.t()), late.contains(w.t()), dates, firmRows, maintain, landLeft)) weighed.add(w);
        }
        // ...and the city's gravel roads, paved (0.7.70): no ground to fit.
        Weighed paving = m.kind() == Kind.ROADS && !noPaving ? weighPaving(game, m, site, landLeft, slack, maintain, cashLeft) : null;
        if (paving != null) weighed.add(paving);
        if (weighed.isEmpty()) return power ? spread(game, need, m, site, before, whenOnSite, cashLeft, landLeft, skip) : null;
        /*
         * JUDGEMENTS, in this order: one that can keep the need ahead beats
         * one that cannot; within that tier the whole plan's net present
         * value wins (HW1). Land's cost is included; needing to buy it does
         * not override the sum. A tie keeps
         * the catalogue's order (the sort is stable).
         */
        weighed.sort(java.util.Comparator.comparingInt(Weighed::tier).thenComparingDouble(Weighed::rank));
        if (power) placeFarms(game, weighed, opened, leads, landLeft);
        Weighed best = weighed.get(0);
        // Plan the chosen work before its actual order can open. A cheaper small
        // alternative can wait for its own lead, while a coal-sized plan starts earlier. (Power's candidates were each
        // tested at their own opening above.)
        if (full && !power && maintained(game, m, site, opening(game, best.lead(), slack))) return null;
        if (power) RESERVE_ORDERS.incrementAndGet();
        Weighed next = weighed.size() > 1 ? weighed.get(1) : null;
        return suggestion(game, need, m, site, before, whenOnSite, best, next, cashLeft, landLeft);
    }

    /** A need's suggestion from the building ranked first, and the one after it (null with none): its quote, its figures and its ground. */
    private static Suggestion suggestion(Game game, CityNeeds.Need need, Measure m, Map<BuildingsTemplate, Long> site,
                                         double before, double whenOnSite, Weighed best, Weighed next,
                                         double cashLeft, double landLeft) {
        Lost lost = next == null ? null
                : next.closes() != best.closes() ? Lost.CANNOT_CLOSE
                : Lost.DEARER;

        long n = best.count();
        if (best.paving()) {
            // A paving (0.7.70): its quote, its gravel roads gone, and the
            // ground it frees taken off the land the ones after it see.
            Game.BuildQuote q = game.quotePave(n);
            boolean credit = q.total > cashLeft;
            Map<BuildingsTemplate, Long> done = times(site, pavingStep(game), n);
            double freed = pavingFrees(game) * (double) n;
            return new Suggestion(need, m, best.t(), n, q.total, before, whenOnSite, figure(game, m, done),
                    figure(game, m, done, new Ahead(best.ahead().months(), best.ahead().k(), 0, best.ahead().powerFactor())),
                    best.closes(), credit, units(site), best.unit(), best.per(), best.ahead(), best.lead(),
                    -freed, -landValue(game, freed, landLeft), 0,
                    next == null ? null : next.t(), next == null ? Double.NaN : next.per(), lost,
                    credit ? q.total - Math.max(0, cashLeft) : 0, true, best.value(), next == null ? null : next.value());
        }
        Game.BuildQuote q = game.quoteBuild(best.t(), n);
        boolean credit = q.total > cashLeft;
        double after = figure(game, m, plus(site, best.t(), n));
        Map<BuildingsTemplate, Long> dated = m.kind() == Kind.POWER ? WorksValue.openBy(game, site, best.ahead().months()) : site;
        double atOpening = figure(game, m, plus(dated, best.t(), n), new Ahead(best.ahead().months(), best.ahead().k(), 0, best.ahead().powerFactor()));
        double sq = best.t().getLandSqFt() * (double) n;
        return new Suggestion(need, m, best.t(), n, q.total, before, whenOnSite, after, atOpening,
                best.closes(), credit, units(site), best.unit(), best.per(), best.ahead(), best.lead(),
                sq, best.value().land(),
                Math.max(0, game.landNeededFor(best.t(), n) - availableGround(game, best.t(), landLeft)),
                next == null ? null : next.t(), next == null ? Double.NaN : next.per(), lost,
                credit ? q.total - Math.max(0, cashLeft) : 0, false, best.value(), next == null ? null : next.value());
    }

    /* =====================================================================
       THE POWER PLANNER (PW3, 2026-11-23; spec-electricity 2.8)

       Two rules replace HW1's life-cost ranking for power, which treated a
       wind farm's average as firm and ranked a 325 MW coal plant by its price
       a kW whatever slice of the load it would serve.

       1. THE RESERVE TEST. The firm supply (THE FIRM SUPPLY) must cover the
          year's peak hour with the player's reserve at each order's own
          opening, counting a plant on site only from when it opens. While it
          does not, the order is the plant cheapest a firm kW-year in the
          SLICE the gap lies in - all year where the firm supply is under the
          base block's load, the shoulder's and the peak's hours where it is
          under the shoulder's, the peak's alone above - its capital at the
          real rate over its life (lifeFactor()), its fixed running and its
          fuel for those hours, over the firm kW the gap can use (a unit
          bigger than the gap pays for its idle part): the screening curve.
          That curve picks among the firm rows (everything but wind and
          solar), HW1's tier of those that close the gap first.
          A wind or solar farm is a candidate at the credit the firm supply
          gives it (planners credit them at their ELCC toward the reserve
          margin: NREL, docs.nlr.gov/docs/fy14osti/61182.pdf), a credit that
          falls as the variable rows' share of the energy rises; its count is
          held to the farms that keep that share at or under
          PowerDispatch.CREDIT_HALF (reserveMost()), where the credit has
          reached its floor and a fleet adds only a tenth: so wind alone
          never passes the test. A farm whose credit closes the gap is
          compared with the screening curve's firm pick on total net cost
          (reserveRank(): the year's capital at the real rate and fixed
          running, less the year's running it saves, the twelve months
          dispatched with it as the spread reads it), so that a farm carries
          the thermal capital it avoids (PJM's Net CONE: the cost of new
          entry less its energy margins): the farm that costs less is the
          reserve order, the lowest of several, and with no firm row closing
          the farms rank among themselves by it. A farm that cannot close is
          ranked after the firm rows that do, by its net cost a firm kW it
          adds at its own opening. A gap under FIRST_WORK_SHARE of what the
          order adds is a need only when the seasonal peak
          (PowerDispatch.PLAN_PEAK_SEASON: the peak block of the year's
          highest month) is short at the plant's own completion, its lead
          and the town's growth over it, no horizon and no slack: a gap the
          opening's horizon and slack alone project waits (2026-11-24: an
          order for a 29 kW gap, 60 months out, borrowed $8.1M).
          THE NEED DATE (REV-PW3f): a row that passes is due, the date the
          forecast first falls short at or before its completion; it orders
          only when no faster firm row, which could still be on line by
          then, meets the need at its opening for less (timely()), and a
          row due by the waiver alone and already past its last on-time
          month is no candidate while a faster row can still be on line by
          the date, whatever it costs. So a
          long-lead unit is committed early only as the least-cost way to
          meet the date, a few-MW gap waits for the diesel's own last
          on-time month, and a month already short orders the cheapest at
          once (REV-PW3d, an 86,181 kW plant for towns drawing 3,000 kW;
          REV-PW3e, every diesel a lead late; REV-PW3f, the Oil-Fired Plant
          for a gap the diesel could still meet). The
          month's own reading of "short" is lacksNow(): that same block, which
          the dispatch serves at its worst, not the plan's peak hour, so the
          holds on every other essential order are not set by a reserve the
          month never uses.
       2. THE SPREAD. When the test passes, a plant is still ordered if one
          more saves the year's running more than it costs: the twelve months
          dispatched again with it (UtilitiesHandler.yearRunning(), at today's
          draw), the fuel and the shortage it displaces against its capital
          over its life and its fixed running, the best saving a dollar of
          capital first, one at a time.
       ===================================================================== */

    /** Power's reserve orders suggested (the reserve test fired): PowerPlanCheck's count, never a decision. */
    public static final java.util.concurrent.atomic.AtomicLong RESERVE_ORDERS = new java.util.concurrent.atomic.AtomicLong();

    /** Power's spread orders suggested (the spread fired): PowerPlanCheck's count, never a decision. */
    public static final java.util.concurrent.atomic.AtomicLong SPREAD_ORDERS = new java.util.concurrent.atomic.AtomicLong();

    /** The game's hours a year: twelve months of PowerDispatch.MONTH_HOURS. */
    static final double YEAR_HOURS = 12 * PowerDispatch.MONTH_HOURS;

    /** The slices of the load, the screening curve's (spec 2.8): 0 the base, 1 the shoulder and the peak, 2 the peak alone. */
    public static int slice(double firmKw, double meanDrawKw) {
        double lossless = 1 - PowerDispatch.GRID_LOSSES;
        if (firmKw < meanDrawKw * PowerDispatch.BLOCK_LOAD[PowerDispatch.BASE] / lossless) return 0;
        if (firmKw < meanDrawKw * PowerDispatch.BLOCK_LOAD[PowerDispatch.SHOULDER] / lossless) return 1;
        return 2;
    }

    /** The hours a year a slice's kW is needed: all of them for the base, the shoulder's and the peak's blocks for the shoulder, the peak's alone above (PowerDispatch.BLOCK_HOURS). */
    public static double sliceHours(int slice) {
        double share = 0;
        for (int b = slice == 0 ? 0 : slice == 1 ? PowerDispatch.SHOULDER : PowerDispatch.PEAK; b < PowerDispatch.BLOCKS; b++)
            share += PowerDispatch.BLOCK_HOURS[b];
        return share * YEAR_HOURS;
    }

    /**
     * The most of a power plant the reserve test counts with `at` open (PW3, REV-PW3 #3): a hydro station's the slots its river has free
     * (HY1); a wind or solar farm's the farms that keep the variable rows' share of the energy (variableShare(), the figure the credit
     * falls by) at or under PowerDispatch.CREDIT_HALF, past which a fleet adds only CREDIT_FLOOR's tenth, no reserve a planner builds;
     * every other row MOST. 0 skips the candidate.
     */
    static long reserveMost(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Long> at) {
        if (t.isHydro()) return Math.min(MOST, game.hydroSlotsFree(t));
        if (!(t.isWind() || t.isSolar())) return MOST;
        double have = variableShare(game, at), each = variableShare(game, plus(at, t, 1)) - have;
        // A city drawing nothing has no share to fall by.
        if (!(each > 0)) return MOST;
        return Math.max(0, Math.min(MOST, (long) Math.floor((PowerDispatch.CREDIT_HALF - have) / each + 1e-9)));
    }

    /** What a kWh a plant runs costs the city: a fuelled row's fuel and variable upkeep (UtilitiesHandler.marginalOf()), a station's variable upkeep, a wind or solar farm's nothing. */
    public static double runningPerKwh(Game game, BuildingsTemplate t) {
        return game.getServicesManager().getUtilitiesHandler().runningPerKwh(t);
    }

    /** n of a plant's capital and ground: the quote of the order, its ground at landValue() on the land left - a farm's in the farmland reserve first, at its price (RG1). */
    static double capitalOf(Game game, BuildingsTemplate t, long n, double landLeft) {
        return game.quoteBuild(t, n).total + landValue(game, t, t.getLandSqFt() * (double) n, landLeft + reserveHeldFor(game, t));
    }

    /** What n of a plant cost a year: twelve months of its `capital` over lifeFactor() `life` and of its fixed running (the spread's cost, and the reserve rank's). */
    static double yearCost(Game game, BuildingsTemplate t, long n, double capital, double life) {
        return 12 * (capital / life + n * runningCostAt(game, NewUnit.of(game, t), 0));
    }

    /**
     * n of a plant's price a firm kW-year for the slice the gap at `p`'s demand lies in, with `at` open (the screening curve, rule 1):
     * twelve months of its capital and ground over lifeFactor() and of its fixed running (yearCost()), and its running a kWh for the
     * slice's hours, over the firm kW of the gap it serves; +infinity for one that adds no firm kW. The firm rows' rank.
     */
    static double screening(Game game, BuildingsTemplate t, long n, Map<BuildingsTemplate, Long> at, Ahead p, double landLeft) {
        Measure m = Measure.of(Kind.POWER);
        double[] sd = supplyDemand(game, m, at, p);
        double firm = n * unit(game, m, t), used = Math.min(firm, Math.max(0, sd[1] - sd[0]));
        if (!(used > 0)) return Double.POSITIVE_INFINITY;
        double hours = sliceHours(slice(sd[0], sd[1] / (PowerDispatch.PLAN_PEAK_HOUR * p.powerFactor())));
        double year = yearCost(game, t, n, capitalOf(game, t, n, landLeft), lifeFactor(game));
        return (year + runningPerKwh(game, t) * used * hours) / used;
    }

    /**
     * A plant's net cost a year (PW3, REV-PW3 #3), lower first: n of it, its yearCost() less the year's running it saves
     * (UtilitiesHandler.yearRunning() on what is on site by `lead`, the plant's own opening, as spreads() reads it). A wind or solar
     * farm's rank, and the firm pick's to weigh it against (placeFarms()). For one that `closes` the gap it is the total; for one
     * that does not, that net cost a firm kW added (PJM's Net CONE), over the firm supply with it at `at` open at `p`'s demand
     * less without. +infinity for one that adds no firm kW.
     */
    static double reserveRank(Game game, BuildingsTemplate t, long n, boolean closes, double lead, Map<BuildingsTemplate, Long> at, Ahead p,
                              double landLeft) {
        Measure m = Measure.of(Kind.POWER);
        double added = supplyDemand(game, m, plus(at, t, n), p)[0] - supplyDemand(game, m, at, p)[0];
        if (!(added > 0)) return Double.POSITIVE_INFINITY;
        UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
        Map<BuildingsTemplate, Long> base = WorksValue.openBy(game, onSite(game, m), lead);
        double saved = u.yearRunning(base) - u.yearRunning(plus(base, t, n));
        double net = yearCost(game, t, n, capitalOf(game, t, n, landLeft), lifeFactor(game)) - saved;
        return closes ? net : net / added;
    }

    /** One whole monthly constraint; its Ahead carries the actual demand factor into counts, guards, rank and display. */
    private record PowerRequirement(Map<BuildingsTemplate, Long> at, Ahead ahead, long[] count) { }

    /** One actual count and quote, validated against every applicable monthly requirement. */
    private record PowerPlan(long count, double lead, PowerRequirement binding, boolean closes) { }

    private static List<PowerRequirement> powerRequirements(Game game, Measure m, BuildingsTemplate t,
                                                             WorksValue.PowerDates dates, double lead, double slack, boolean maintain) {
        double from = Double.isNaN(lead) ? 0 : Math.max(0, lead);
        int peak = dates.nextPeak(from);
        List<PowerRequirement> tests = new ArrayList<>();
        long most = MOST;
        for (double month : dates.frontier(from)) most = Math.min(most, reserveMost(game, t, dates.openBy(month)));
        for (double month : dates.frontier(from)) {
            Map<BuildingsTemplate, Long> at = dates.openBy(month);
            double factor = month == peak ? 1 : PowerDispatch.PLAN_PEAK
                    * PowerDispatch.season(CityCalendar.monthOfYear(game.getMonth() + (int) month),
                            game.getServicesManager().getUtilitiesHandler().latitude()) / PowerDispatch.PLAN_PEAK_HOUR;
            Ahead p = new Ahead(month, game.getBusinessInvestment().growthFactor(month), month == peak ? slack : 0, factor);
            long[] n = powerCovered(game, m, at, p, maintain) ? new long[] {0, 1}
                    : count(game, m, at, Map.of(t, 1L), p, most, maintain);
            tests.add(new PowerRequirement(at, p, n));
        }
        return tests;
    }

    private static boolean powerCovered(Game game, Measure m, Map<BuildingsTemplate, Long> at, Ahead p, boolean maintain) {
        return maintain ? maintained(game, m, at, p) : ahead(game, m, at, p);
    }

    /**
     * Search distinct dated requirements, not a count that can only rise. Each actual count is quoted once and may add
     * new per-date counts to the finite worklist (1..MOST). A cycle cannot repeat; the least feasible evaluated count
     * wins. Falling farm credit is checked again at the actual quoted count over every date, never inferred from a maximum.
     */
    private static PowerPlan powerPlan(Game game, Measure m, BuildingsTemplate t, WorksValue.PowerDates dates,
                                       double slack, boolean maintain) {
        java.util.SortedSet<Long> pending = new java.util.TreeSet<>();
        java.util.Set<Long> seen = new java.util.HashSet<>();
        pending.add(1L);
        PowerPlan best = null, partial = null;
        while (!pending.isEmpty()) {
            long n = pending.first();
            pending.remove(n);
            if (n < 1 || n > MOST || !seen.add(n)) continue;
            double lead = game.quoteBuild(t, n).months;
            if (Double.isInfinite(lead) || lead > Integer.MAX_VALUE - game.getMonth() - 12.0) continue;
            List<PowerRequirement> tests = powerRequirements(game, m, t, dates, lead, slack, maintain);
            PowerRequirement binding = null;
            double worst = Double.NEGATIVE_INFINITY;
            long most = MOST, largest = 0;
            boolean need = false, closes = true, allCanClose = true;
            for (PowerRequirement test : tests) {
                long required = test.count()[0];
                if (required > 0 && !seen.contains(required)) pending.add(required);
                most = Math.min(most, reserveMost(game, t, test.at()));
                double[] sd = supplyDemand(game, m, test.at(), test.ahead());
                double gap = sd[1] - sd[0];
                need |= !powerCovered(game, m, test.at(), test.ahead(), maintain);
                allCanClose &= test.count()[1] == 1;
                closes &= powerCovered(game, m, plus(test.at(), t, n), test.ahead(), maintain);
                if (binding == null || required > largest || required == largest && gap > worst) {
                    binding = test;
                    largest = required;
                    worst = gap;
                }
            }
            if (most > 0 && !seen.contains(most) && most < n) pending.add(most);
            if (!need || n > most) continue;
            PowerPlan plan = new PowerPlan(n, lead, binding, closes);
            if (closes && (best == null || n < best.count())) best = plan;
            // Preserve a physically limited farm's cannot-close tier, without calling an unproved firm count a closer.
            if (!allCanClose && n == largest && (partial == null || n < partial.count())) partial = plan;
        }
        PowerPlan plan = best != null ? best : partial;
        if (plan == null || Double.compare(plan.lead(), game.quoteBuild(t, plan.count()).months) != 0) return null;
        return plan;
    }

    /**
     * Whether a power row that passed its own test, so is due (the need date falls at or before its completion), orders now: THE NEED
     * DATE (PW3, REV-PW3f). Reality's rule, written once: one need date, the first month the forecast falls short; the candidates the
     * firm rows that can be on line by then; among them the least-cost by the screening curve, a long-lead unit committed early only as
     * the least-cost way to meet the date; a few-MW gap a diesel, ordered at the last month it can still be on time; a month already
     * short ordering at once. Each row's own test reads the date at its own completion, so the rows that can still be on line by it are
     * the faster ones. `d` waits (false) while a faster firm row:
     * - can still be on line by the need date, its completion covered, when `d` is `late` (due by the waiver alone, the seasonal peak
     *   short a month before its own completion too): `d` is no candidate, whatever it costs;
     * - or, its order for `d`'s binding dated gap (`at`, `d.ahead()`) built within `d`'s own lead, closes a gap `d` does not (HW1's
     *   tier) or closes it at a lower cost: the screening curve against a firm row's, the total net cost against a farm's
     *   (placeFarms()'s comparison).
     * That row orders at its own last on-time month instead, when it is due itself. In a month already short every row is late and none
     * covered at its completion, and the cheapest orders at once. The native schedule reads a faster row's actual completion too.
     * `firm` maps each firm row to the months until the menu brings it, which its completion counts from (0 for a row known now).
     */
    private static boolean timely(Game game, Measure m, Weighed d, Map<BuildingsTemplate, Long> at, boolean late,
                                  WorksValue.PowerDates dates, Map<BuildingsTemplate, Integer> firm,
                                  boolean maintain, double landLeft) {
        for (Map.Entry<BuildingsTemplate, Integer> row : firm.entrySet()) {
            BuildingsTemplate u = row.getKey();
            Game.BuildQuote first = game.quoteBuild(u, 1);
            // A future menu row must pass the same first-unit ground and funding screen as an order now.
            // Otherwise an unaffordable future plan can veto the cash-funded partial step that the pass can place.
            if (row.getValue() > 0) {
                AutoBuilder.Ground ground = AutoBuilder.groundFor(game, u, 1);
                if (ground == null || !game.getAutoBuilder().pays(game, ground.cash() + first.total, false)) continue;
            }
            double one = row.getValue() + first.months;
            if (u == d.t() || !(one < d.lead())) continue;
            double monthly = Double.isNaN(one) ? 0 : Math.ceil(Math.max(0, one));
            if (late && !seasonalShort(game, m, dates.openBy(monthly), monthly)) return false;
            long[] c = count(game, m, at, plus(new LinkedHashMap<>(), u, 1), d.ahead(), reserveMost(game, u, at), maintain);
            if (c[0] <= 0 || c[1] != 1) continue;
            double soon = row.getValue() + game.quoteBuild(u, c[0]).months;
            if (!(soon < d.lead())) continue;
            if (!d.closes()) return false;
            double cost = isFarm(d.t()) ? reserveRank(game, u, c[0], true, soon, at, d.ahead(), landLeft)
                    : screening(game, u, c[0], at, d.ahead(), landLeft);
            if (cost < d.rank()) return false;
        }
        return true;
    }

    /**
     * Whether the seasonal peak (PowerDispatch.PLAN_PEAK_SEASON, the peak block of the year's highest month) is short `months` on, with
     * `at` open: the town's growth to then (BusinessInvestment.growthFactor()), no horizon past it and no slack. A row's completion in
     * suggestFor()'s own test, and THE NEED DATE's reading of which rows can still be on line by it (timely()).
     */
    static boolean seasonalShort(Game game, Measure m, Map<BuildingsTemplate, Long> at, double months) {
        double[] sd = supplyDemand(game, m, at, new Ahead(months, game.getBusinessInvestment().growthFactor(months), 0));
        return sd[1] > 0 && sd[0] < sd[1] * PowerDispatch.PLAN_PEAK_SEASON / PowerDispatch.PLAN_PEAK_HOUR;
    }

    /** A wind or solar farm: the rows whose credit falls with their share (reserveMost(), placeFarms()). */
    private static boolean isFarm(BuildingsTemplate t) { return t.isWind() || t.isSolar(); }

    /**
     * Where the farms that close the gap stand among the sorted candidates (PW3, REV-PW3 #3; the reviewer's: a farm's credit avoids a
     * thermal plant's capital). `weighed` is sorted by tier and rank, the firm closers first by the screening curve; a closing farm
     * carries its total net cost as its rank (reserveRank()). The farms whose total is below the firm pick's, computed by the same
     * formula at the pick's own n and lead, go first, the lowest first; then the firm closers; then the dearer farms; then the rest
     * (HW1's second tier). With no firm closer the farms lead, by their totals.
     */
    private static void placeFarms(Game game, List<Weighed> weighed, Map<BuildingsTemplate, Map<BuildingsTemplate, Long>> opened,
                                   Map<BuildingsTemplate, Double> leads, double landLeft) {
        List<Weighed> farms = new ArrayList<>(), rest = new ArrayList<>();
        for (Weighed w : weighed) (w.closes() && isFarm(w.t()) ? farms : rest).add(w);
        if (farms.isEmpty()) return;
        farms.sort(java.util.Comparator.comparingDouble(Weighed::rank));
        Weighed pick = rest.isEmpty() || !rest.get(0).closes() ? null : rest.get(0);
        double firm = pick == null ? Double.POSITIVE_INFINITY
                : reserveRank(game, pick.t(), pick.count(), true, leads.get(pick.t()), opened.get(pick.t()), pick.ahead(), landLeft);
        List<Weighed> order = new ArrayList<>();
        int f = 0, r = 0;
        while (f < farms.size() && farms.get(f).rank() < firm) order.add(farms.get(f++));
        while (r < rest.size() && rest.get(r).closes()) order.add(rest.get(r++));
        while (f < farms.size()) order.add(farms.get(f++));
        while (r < rest.size()) order.add(rest.get(r++));
        weighed.clear();
        weighed.addAll(order);
    }

    /** One more of a plant weighed by the spread (rule 2): the year's running it saves, what it costs a year, and the net over its capital. */
    public record Spread(BuildingsTemplate template, double saved, double cost, double capital) {
        /** What it saves a year less what it costs a year, city money. */
        public double net() { return saved - cost; }
        /** ...over its capital and ground: the saving a dollar, what the spread ranks by. */
        public double ratio() { return capital > 0 ? net() / capital : Double.NEGATIVE_INFINITY; }
    }

    /**
     * Every power plant the city could order one more of, weighed by the spread with what is on site standing at that plant's own
     * opening (WorksValue.openBy() at its quoted lead, as the reserve test reads a site; PW3, REV-PW3 #4); `skip` left out.
     */
    public static List<Spread> spreads(Game game, double landLeft, java.util.Set<BuildingsTemplate> skip) {
        Measure m = Measure.of(Kind.POWER);
        UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
        Map<BuildingsTemplate, Long> site = onSite(game, m);
        double life = lifeFactor(game);
        List<Spread> out = new ArrayList<>();
        for (BuildingsTemplate t : game.getBuildingManager().getKnownTemplates()) {
            if (!m.serves(t) || skip.contains(t) || !(unit(game, m, t) > 0)) continue;
            // PW3: a plant on site counts from its opening, as the reserve test reads it: one that opens after this plant does is not running
            // when this one is, so it takes none of its saving (nor blocks it for years while its site is delayed).
            Map<BuildingsTemplate, Long> base = WorksValue.openBy(game, site, game.quoteBuild(t, 1).months);
            double saved = u.yearRunning(base) - u.yearRunning(plus(base, t, 1));
            double capital = capitalOf(game, t, 1, landLeft);
            double cost = yearCost(game, t, 1, capital, life);
            out.add(new Spread(t, saved, cost, capital));
        }
        return out;
    }

    /** Power with the reserve test passed (rule 2): one of the plant whose next unit saves the most a dollar of capital, when it saves more than it costs; null when none does. */
    private static Suggestion spread(Game game, CityNeeds.Need need, Measure m, Map<BuildingsTemplate, Long> site,
                                     double before, double whenOnSite, double cashLeft, double landLeft,
                                     java.util.Set<BuildingsTemplate> skip) {
        List<Spread> all = spreads(game, landLeft, skip);
        all.sort(java.util.Comparator.comparingDouble(Spread::ratio).reversed());
        Spread top = all.isEmpty() ? null : all.get(0);
        if (top == null || !(top.net() > 0)) return null;
        BuildingsTemplate t = top.template();
        double lead = game.quoteBuild(t, 1).months;
        Ahead p = opening(game, lead, game.getAutoBuilder().getSlack());
        WorksValue.Value value = WorksValue.of(game, m, onSite(game, m), t, 1, landLeft, cashLeft);
        boolean fits = game.landNeededFor(t, 1) <= availableGround(game, t, landLeft);
        Weighed best = new Weighed(t, 0, value.perUsed(), unit(game, m, t), 1, true, fits, p, lead, false, value, -top.ratio());
        Weighed next = null;
        if (all.size() > 1 && all.get(1).net() > 0) {
            Spread s2 = all.get(1);
            WorksValue.Value v2 = WorksValue.of(game, m, onSite(game, m), s2.template(), 1, landLeft, cashLeft);
            next = new Weighed(s2.template(), 0, v2.perUsed(), unit(game, m, s2.template()), 1, true, true, p,
                    game.quoteBuild(s2.template(), 1).months, false, v2, -s2.ratio());
        }
        SPREAD_ORDERS.incrementAndGet();
        return suggestion(game, need, m, site, before, whenOnSite, best, next, cashLeft, landLeft);
    }

    /**
     * The count before 0.7.51, kept for the harness's comparison: the least
     * n whose figure today is off the list (count() at no projection and no
     * slack, judged by clear() alone).
     */
    static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t) {
        return count(game, m, site, t, null);
    }

    /**
     * The count of t that keeps the need ahead at p's demand, after what is
     * on site (p null: the old rule, off the list today). {n, 1} when one
     * exists; when none does - the figure stops improving first - {n, 0}, n
     * the least count that reaches the best figure this building can make.
     */
    static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t, Ahead p) {
        // A hydro station's count is held to the slots free (HY1): never more than the city's river holds.
        long most = m.kind() == Kind.POWER && t.isHydro() ? Math.min(MOST, game.hydroSlotsFree(t)) : MOST;
        return count(game, m, site, plus(new LinkedHashMap<>(), t, 1), p, most);
    }

    /**
     * ...of a step that may be more than one building (0.7.70): a paving
     * is a Paved Road more and a Gravel Road less (pavingStep()); and at most
     * `most` of it, the gravel roads there are to pave. The search the count
     * always made, n doubling to MOST: to the bit the same for one building.
     */
    static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> step,
                       Ahead p, long most) {
        return count(game, m, site, step, p, most, false);
    }

    /** Count full staffed service for automatic essential works; other advice retains its own figure (HW1). */
    static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> step,
                               Ahead p, long most, boolean maintain) {
        if (most < 1) return new long[] {0, 0};
        Ahead at = p == null ? NOW : p;
        boolean full = maintain && AutoBuilder.essential(m);
        java.util.function.Predicate<Map<BuildingsTemplate, Long>> done = full ? a -> maintained(game, m, a, at)
                : p == null ? a -> clear(m, figure(game, m, a)) : a -> ahead(game, m, a, at);
        java.util.function.ToDoubleFunction<Map<BuildingsTemplate, Long>> reading = a -> {
            if (!full) return figure(game, m, a, at);
            double[] sd = supplyDemand(game, m, a, at);
            return sd[0] - sd[1];
        };
        double before = reading.applyAsDouble(site);
        boolean worseHigher = !full && higherWorse(m);
        double best = before;
        long tried = 0, n = 1;
        while (true) {
            Map<BuildingsTemplate, Long> a = times(site, step, n);
            double f = reading.applyAsDouble(a);
            if (done.test(a)) {
                long lo = tried, hi = n;           // lo fails (or is 0), hi is done
                while (hi - lo > 1) {
                    long mid = lo + (hi - lo) / 2;
                    if (done.test(times(site, step, mid))) hi = mid; else lo = mid;
                }
                return new long[] {hi, 1};
            }
            boolean improved = worseHigher ? f < best - 1e-12 : f > best + 1e-12;
            if (!improved) break;
            best = f;
            tried = n;
            if (n >= most) break;
            n = Math.min(2L * n, most);
        }
        if (tried == 0) return new long[] {0, 0};
        // The least count that reaches the best this building made.
        final double target = best;
        long lo = 0, hi = tried;
        while (hi - lo > 1) {
            long mid = lo + (hi - lo) / 2;
            double f = reading.applyAsDouble(times(site, step, mid));
            boolean there = worseHigher ? f <= target + 1e-12 : f >= target - 1e-12;
            if (there) hi = mid; else lo = mid;
        }
        return new long[] {hi, 0};
    }

    /** A copy of base with n times every quantity of step added. */
    static Map<BuildingsTemplate, Long> times(Map<BuildingsTemplate, Long> base, Map<BuildingsTemplate, Long> step, long n) {
        Map<BuildingsTemplate, Long> out = new LinkedHashMap<>(base);
        if (n != 0) for (Map.Entry<BuildingsTemplate, Long> e : step.entrySet()) out.merge(e.getKey(), n * e.getValue(), Long::sum);
        return out;
    }

    /* =====================================================================
       THE SIZE THAT FITS THE NEED (0.7.71)

       Jerus: "one city had 5k daycares and 2k residential buildings". The
       advice ranked a care building by its price a place with its ground,
       and a Home Daycare's was the lowest, so it ordered them by the
       hundred (629 in his city of 24,000). The three childcare buildings
       are centres of 80, 220 and 360 places since 0.7.71, a place cheaper
       the bigger (BuildingManager, childcare resized), and ranked a place
       the largest would win everywhere: a 360-place centre for a town short
       of thirty children. So from 0.7.71 to 0.7.100 a living care building -
       childcare, general or senior - was ranked by what its order costs over
       the places the need lacks at the demand it is sized to
       (perPlaceNeeded, gone): the order's own quote - Game.quoteBuild() for
       the count, which takes the yard's material once, where a quote for one
       takes it for every building - and its ground, over that demand less
       what is on site. Where the order is many buildings that is its price a
       place, and the cheapest a place wins - a big city builds big centres;
       where one building is more than the need, the places past it are paid
       for, so the smallest that closes the gap wins. Since 0.7.101 every
       candidate is weighed so, over its life (A BUILDING OVER ITS LIFE).
       ===================================================================== */

    /* =====================================================================
       A ROAD OVER ITS LIFE (0.7.70)

       Jerus: "the game still recommends gravel roads, even when i think paved
       roads are better". The road's candidates - the three roads and the
       three lines - were ranked from 0.7.51 by their price for one with its
       ground, over the trips one takes off the road: the capital only. A
       road is also repaired every year it stands (one percent of what it
       cost, the rule every city building is charged by), it draws power,
       and a line pays its crews and takes fares. So since 0.7.70 each is
       priced over its life (lifetime()): its quote and its ground at
       landValue(), and a month of running it (running()) for LIFE_MONTHS,
       discounted at the real rate the city's money costs at that term
       (lifeFactor()). One figure ranks the advice's roads, draws the road
       cards' first bar (BuildCard) and orders the test player's roads
       (LongPlaytest.addRoadThrottle()). The trips a road takes off are the
       model's own (unit()), its freight grade in them.

       AND A GRAVEL ROAD THE CITY HAS CAN BE PAVED (ConstructionControl, F):
       a candidate beside the streets' five (the six until FR7, the highway
       now the HIGHWAYS measure's; weighPaving()), priced the same way for
       the trips its paving adds, the ground it frees taken off at the price
       the next road would pay for it (landValue() on the land left) - so it
       wins when land is dear, and a paving the gravel roads standing cannot
       close loses to a road that can, as any candidate does.

       WHAT THE NUMBERS SAID (runs/fixN1-notes.md, 0): the advice was not
       what recommended gravel - it has priced the ground since 0.7.51, and in
       Jerus's city it picks paved roads - but the road cards tagged gravel
       "cheapest per trip" on the price alone in every city, and the test
       player built only gravel, ranking the roads by trips a founding
       dollar. Over its life a gravel road costs less to keep - its repairs
       are a share of its smaller price - so the life moves the line toward
       gravel, not away: a paved road wins where a gravel road's ground is
       worth about 1.4 to 1.7 times its price.

       EACH ROAD ITS OWN REPAIRS (0.7.103, batch P4; Jerus's A11): the share
       a year is the road kind's own (BuildingsTemplate.repairsPerYear() -
       gravel 3%, paved 2%, the highway 1.25%, where every road was at the
       house's 1%), and running() reads it. A gravel road is still a little
       the cheaper to keep a trip it carries (its 900 trips are three
       quarters of a paved road's, where real gravel carries far less), but
       the gap is a seventh where it was nearly a half.

       PAVING BY TRAFFIC (FR7 2.2, 2026-10-18; Jerus's star 5). A gravel
       road's repairs rise with the traffic on its street (Minnesota's
       finding: its upkeep climbs sharply past about 200 vehicles a day;
       InfrastructureManager, REPAIRS BY TRAFFIC), and running() charges it so,
       at the street utilisation the city would have with the road added
       (BuildingsTemplate.gravelRepairsAt()); a gravel road already standing,
       as a paving replaces one, is charged at the city as it is. So the same
       life-cost ranking that stood picks paved where the streets are busy and
       gravel at the quiet edge, and weighPaving()/pavingBeatsPaved() offer
       the paving by the load and not by a land price - no modifier anywhere.
       The paved road's and the highway's repairs stay flat.
       ===================================================================== */

    /** Months a road is weighed over (0.7.70): the funding page's bond term, Game.BUILD_BOND_YEARS - a long-lived asset "paid for over the years the city uses it". */
    public static final int LIFE_MONTHS = Game.BUILD_BOND_YEARS * 12;

    /**
     * What one costs the city a month, standing (0.7.70): its repairs - its
     * share a year of what one costs to put up today (since 0.7.103 a road
     * kind's own, BuildingsTemplate.repairsPerYear(); the house's one percent
     * for every other), its work and its
     * material, with the builders' tax: the rule the month charges every
     * city building by (EconomyManager.maintenanceBillFor(),
     * RealEstate.MAINTENANCE_PER_YEAR) - its power and water at the
     * utility's prices (BusinessInvestment.runningCostOf()'s reading), its
     * posts at today's wages and its upkeep (BuildCard.runningCost()), less a
     * month of fares from the riders it adds on the network with what is on
     * site (TaxPolicy.monthlyFare(), LongPlaytest.linesThatPay()'s reading):
     * a line's, and a road's only where the lines wait for road under them.
     */
    public static double running(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Long> site) {
        return running(game, NewUnit.of(game, t), site, true);
    }

    /** ...of one new building already resolved (QN1): its own posts, power and water. */
    public static double running(Game game, NewUnit u, Map<BuildingsTemplate, Long> site) {
        return running(game, u, site, true);
    }

    /**
     * ...and, for a Gravel Road, at the traffic on the streets it is repaired
     * at (FR7 2.2, BuildingsTemplate.gravelRepairsAt()): the streets'
     * utilisation of the city with what is on site and, when `added`, the
     * road itself too - the what-if of ordering it; a gravel road already
     * standing (a paving's, pavingOrderLife()) is repaired at the city as it
     * is with what is on site. Every other building's repairs are flat, and
     * on quiet streets a gravel road's are, to the bit.
     */
    public static double running(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Long> site, boolean added) {
        // ...a building already standing (`added` false: a paving's gravel road, pavingOrderLife()) keeps the figures it stands with (QN1).
        return running(game, added ? NewUnit.of(game, t) : NewUnit.standing(t), site, added);
    }

    /** ...of one new building already resolved (QN1). */
    public static double running(Game game, NewUnit u, Map<BuildingsTemplate, Long> site, boolean added) {
        BuildingsTemplate t = u.template();
        double utilisation = roads(game, added ? plus(site, t, 1) : site).getUtilisation();
        double riders = roads(game, plus(site, t, 1)).getTransitRiders() - roads(game, site).getTransitRiders();
        return runningCostAt(game, u, utilisation) - riders * game.getEconomyManager().getTaxPolicy().monthlyFare();
    }

    /**
     * The running cost the planners read: the building's own (rawRunningCostAt()) with a patented public work's licence added (R2c-P,
     * LicenceFees.forNewWork()), so WorksValue and the auto-builder net the rent a new utility would pay.
     */
    private static double runningCostAt(Game game, BuildingsTemplate t, double streetUtilisation) {
        return runningCostAt(game, NewUnit.standing(t), streetUtilisation);
    }

    /** ...of one new building already resolved (QN1); the licence is the template's. */
    private static double runningCostAt(Game game, NewUnit u, double streetUtilisation) {
        double raw = rawRunningCostAt(game, u, streetUtilisation);
        return raw + LicenceFees.forNewWork(game, u.template(), raw);
    }

    /**
     * The licence running() adds for one more of this building (R2c-P, REV-R2c-P-1): LicenceFees.forNewWork() on its raw running cost, over a
     * life; 0 for any work that is not one of a 1900 city's own utilities, and in any other city.
     */
    static double licenceOf(Game game, BuildingsTemplate t) {
        return licenceOf(game, NewUnit.of(game, t));
    }

    /** ...of one new building already resolved (QN1): the licence on its own running cost, as running(game, u, ...) adds it. */
    static double licenceOf(Game game, NewUnit u) {
        BuildingsTemplate t = u.template();
        if (!game.foundedUnfitted() || !LicenceFees.citysWork(game, t)) return 0;
        return LicenceFees.forNewWork(game, t, rawRunningCostAt(game, u, 0));
    }

    /**
     * The running cost of one building before any licence (R2c-P: runningCostAt() adds a public work's), at this street utilisation:
     * the template as it stands (LicenceFees and RecipeChanges.feesAt() price the buildings they are given).
     */
    static double rawRunningCostAt(Game game, BuildingsTemplate t, double streetUtilisation) {
        return rawRunningCostAt(game, NewUnit.standing(t), streetUtilisation);
    }

    /** ...of one new building already resolved (QN1): its own power, water, posts; its repairs and upkeep are the template's. */
    static double rawRunningCostAt(Game game, NewUnit u, double streetUtilisation) {
        BuildingsTemplate t = u.template();
        BuildingManager bm = game.getBuildingManager();
        EconomyManager econ = game.getEconomyManager();
        double materials = Math.max(0, bm.getConstructionMaterialPrice());
        double share = ConstructionControl.paves(t) ? t.gravelRepairsAt(streetUtilisation) : t.repairsPerYear();
        double repairs = econ.withBuildersTax(bm.nonMaterialCost(t) + t.getConstructionMaterials() * materials)
                * share / 12;
        double utilities = u.getElectricityConsumption() * econ.getPowerPriceFor(t)
                + u.getWaterConsumption() * econ.getPricePerWaterUnit();
        return repairs + utilities + BuildCard.runningCost(game, u);
    }

    /** The complete stock change's bill, with its actual traffic wear and one shared pool of fare-paying riders. */
    static double runningChange(Game game, Map<BuildingsTemplate, Long> before,
                                Map<BuildingsTemplate, Long> after, Ahead at) {
        InfrastructureManager r0 = roads(game, before, at.scale()), r1 = roads(game, after, at.scale());
        Map<BuildingsTemplate, Long> change = new LinkedHashMap<>(after);
        for (Map.Entry<BuildingsTemplate, Long> e : before.entrySet())
            change.merge(e.getKey(), -e.getValue(), Long::sum);
        double cost = 0;
        for (Map.Entry<BuildingsTemplate, Long> e : change.entrySet()) {
            long n = e.getValue();
            if (n > 0 && game.foundedUnfitted()) {
                // R2c-P, REV-R2c-P-1: a city utility's licence is the bill of the whole order, at the month it opens, not one more's fee times n.
                double raw = rawRunningCostAt(game, NewUnit.of(game, e.getKey()), r1.getUtilisation());
                cost += n * raw + LicenceFees.forNewWork(game, e.getKey(), raw, n, (int) Math.round(at.months()));
            } else if (n != 0) cost += n * runningCostAt(game, adding(game, e.getKey(), n),
                    n > 0 ? r1.getUtilisation() : r0.getUtilisation());
        }
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (!ConstructionControl.paves(t)) continue;
            long existing = game.getBuildingManager().getQuantity(t.getId()) + before.getOrDefault(t, 0L);
            if (after.getOrDefault(t, 0L) < before.getOrDefault(t, 0L))
                existing += after.getOrDefault(t, 0L) - before.getOrDefault(t, 0L);
            cost += existing * (runningCostAt(game, t, r1.getUtilisation())
                    - runningCostAt(game, t, r0.getUtilisation()));
        }
        double riders = r1.getTransitRiders() - r0.getTransitRiders();
        return cost - riders * game.getEconomyManager().getTaxPolicy().monthlyFare();
    }

    /**
     * Months of a month's running cost a life is worth today (0.7.70): the
     * level annuity's factor over LIFE_MONTHS at the real rate - the debt
     * market's rate for that term at the city's debt now
     * (DebtManager.quoteRate()), less the inflation the city expects
     * (BusinessInvestment.realTestRate(), the businesses' own hurdle) - since
     * the running cost is at today's prices and rises with them. LIFE_MONTHS
     * at a rate of nothing.
     */
    public static double lifeFactor(Game game) {
        double i = lifeRate(game) / 12;
        return i > 0 ? (1 - Math.pow(1 + i, -LIFE_MONTHS)) / i : LIFE_MONTHS;
    }

    /** ...the real rate it is struck at, a year: the debt market's for LIFE_MONTHS less expected inflation (BusinessInvestment.realTestRate()). */
    public static double lifeRate(Game game) {
        game.priceTheDebtMarket();
        return game.getBusinessInvestment().realTestRate(game.getDebtManager().quoteRate(0, LIFE_MONTHS));
    }

    /** A road's or a line's cost over its life (0.7.70): its quote for one, its ground at landValue() on the land left, and lifeFactor() months of running(). */
    public static double lifetime(Game game, BuildingsTemplate t, double landLeft, Map<BuildingsTemplate, Long> site) {
        return lifetime(game, NewUnit.of(game, t), landLeft, site);
    }

    /** ...of one new building already resolved (QN1): its own running cost; its quote and ground are the template's. */
    public static double lifetime(Game game, NewUnit u, double landLeft, Map<BuildingsTemplate, Long> site) {
        BuildingsTemplate t = u.template();
        return game.quoteBuild(t, 1).total + landValue(game, t.getLandSqFt(), landLeft)
                + running(game, u, site) * lifeFactor(game);
    }

    /** One paving (0.7.70): a Paved Road more and a Gravel Road less; empty with either missing from the catalogue. */
    static Map<BuildingsTemplate, Long> pavingStep(Game game) {
        Map<BuildingsTemplate, Long> step = new LinkedHashMap<>();
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        if (from == null || to == null) return step;
        step.put(to, 1L);
        step.put(from, -1L);
        return step;
    }

    /** The ground one paving frees (0.7.70): a gravel road's less a paved road's; 0 with either missing. */
    public static double pavingFrees(Game game) {
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        return from == null || to == null ? 0 : Math.max(0, from.getLandSqFt() - to.getLandSqFt());
    }

    /** The trips one paving takes off the road (0.7.70): over the line with what is on site, less with one more gravel road paved. */
    public static double pavingUnit(Game game, Map<BuildingsTemplate, Long> site) {
        Map<BuildingsTemplate, Long> step = pavingStep(game);
        if (step.isEmpty()) return 0;
        return roadsOver(game, site) - roadsOver(game, times(site, step, 1));
    }

    /**
     * A paving's cost over its life (0.7.70), as lifetime() prices a road:
     * its quote for one (Game.quotePave()), less the ground it frees at
     * landValue() on the land left - what the next road would pay for it -
     * and lifeFactor() months of what a paved road costs to run over a
     * gravel road (running()). NaN with nothing to pave.
     */
    public static double pavingLifetime(Game game, double landLeft, Map<BuildingsTemplate, Long> site) {
        Game.BuildQuote q = game.quotePave(1);
        if (q == null) return Double.NaN;
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        // The gravel road it replaces is repaired at the traffic on the streets as they are (FR7 2.2): the busier, the more paving saves.
        return q.total - landValue(game, pavingFrees(game), landLeft)
                + (running(game, to, site) - running(game, from, site, false)) * lifeFactor(game);
    }

    /**
     * Whether paving beats a new Paved Road (0.7.70), each over its life a
     * trip it takes off: pavingLifetime() over pavingUnit() under a Paved
     * Road's lifetime() over unit() - Jerus's "recommends this if better".
     * False with nothing to pave.
     */
    public static boolean pavingBeatsPaved(Game game, double landLeft, Map<BuildingsTemplate, Long> site) {
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        double unit = pavingUnit(game, site), paved = to == null ? 0 : unit(game, Measure.of(Kind.ROADS), to);
        if (game.paveable() < 1 || !(unit > 0) || !(paved > 0)) return false;
        return pavingLifetime(game, landLeft, site) / unit < lifetime(game, to, landLeft, site) / paved;
    }

    /**
     * The city's gravel roads, paved, weighed for the road (0.7.70) as a
     * building is in suggestFor(): the count that keeps the road ahead at
     * its projection, of the gravel roads the city could pave (Game.paveable());
     * its wait, a Paved Road's; its figure every candidate's since 0.7.101
     * (its order over its life for each trip it will take off -
     * pavingOrderLife(), servedOverLife(); pavingLifetime() over the trips one
     * paving takes off until then); no ground to fit. Weighed only when it
     * beats a new Paved Road a trip over its life (pavingBeatsPaved(), star
     * N1-5): it needs no ground, so it would rank over any road the land left
     * cannot hold, however dear, and it is offered as the cheaper way to the
     * paved road, not as the way round a land purchase. Null otherwise, with
     * none to pave, or a paving that takes nothing off.
     */
    static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Long> site, double landLeft) {
        return weighPaving(game, m, site, landLeft, game.getAutoBuilder().getSlack());
    }

    /** ...at another slack past its projection (0.7.73, AutoBuilder). */
    static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Long> site, double landLeft, double slack) {
        return weighPaving(game, m, site, landLeft, slack, true, game.getCash());
    }

    private static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Long> site,
            double landLeft, double slack, boolean maintain, double cashLeft) {
        long most = game.paveable();
        Map<BuildingsTemplate, Long> step = pavingStep(game);
        if (most < 1 || step.isEmpty()) return null;
        double unit = pavingUnit(game, site);
        if (!(unit > 0) || !pavingBeatsPaved(game, landLeft, site)) return null;
        long[] today = count(game, m, site, step, new Ahead(0, 1, slack), most, maintain);
        if (today[0] <= 0) return null;
        long n = today[0];
        double lead;
        Ahead p;
        long[] found;
        while (true) {
            lead = game.quotePave(n).months;
            p = opening(game, lead, slack);
            found = count(game, m, site, step, p, most, maintain);
            if (found[0] <= n) break;
            n = found[0];
        }
        if (found[0] <= 0) return null;
        boolean closes = found[1] == 1;
        // Its figure since 0.7.101 as every candidate's (A BUILDING OVER ITS LIFE): the pavings' order over its
        // life for each trip it will take off; pavingLifetime() over pavingUnit() until then.
        WorksValue.Value value = WorksValue.paving(game, m, site, found[0], landLeft, cashLeft);
        double per = value.perUsed();
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        return new Weighed(to, closes ? 0 : 1, per, unit, found[0], closes, true, p, lead, true, value, value.net());
    }

    /* =====================================================================
       A BUILDING OVER ITS LIFE, FOR WHAT IT WILL SERVE (0.7.101, batch P2)

       Jerus (the project's decisions-2026-10-10, A4): "it doesnt actually
       target the best, it targets the optimal to satisfy the next few points
       not long run, thus it builds wind farms instead of coal powerplants";
       and A19, of the advice taking a city at 89% served to 670% with a
       $4.95B Coal Power Plant: price the overshoot on power, water and the
       roads as care's (THE SIZE THAT FITS THE NEED) - without stopping a
       first building where there is none. Until now a road was weighed over
       its life (0.7.70), a care building by its order over the places the
       need lacked at its projection (0.7.71), and everything else by its
       quote for one and its ground over what one adds: capital, and only the
       next months' need.

       So every candidate is weighed the one way (lifePerServed()):
         - ITS ORDER OVER ITS LIFE (orderLife()): the quote for the count
           (Game.quoteBuild(), what Build charges), its ground at landValue()
           on the land left, and for each building lifeFactor() months of
           running() - repairs, power and water, posts and upkeep, less fares
           - the road's arithmetic since 0.7.70, over LIFE_MONTHS, the funding
           page's bond term; a paving's (pavingOrderLife()) its quote less the
           ground it frees, and a paved road's running over a gravel road's.
         - OVER WHAT IT WILL SERVE OVER THAT LIFE (servedOverLife()), for the
           measures whose need is a shortfall to fill - power and water (the
           supply the quarter in hand asks, NETWORK_YELLOW, past what stands
           and is on site), the roads (the trips over STRAINED, roadsOver()'s
           line) and care (the places past what stands, the beds less the
           priced out since 0.7.106): in each year of its
           life, from the month it opens, what it takes off that shortfall at
           the demand then - today's grown by the city's own trend (lifeTrend())
           with the slack past it - each year discounted as lifeFactor()
           discounts it. A building bigger than the need is paid for whole and
           counted only for what the city grows into: a coal plant wins where
           the city will use it, and a wind farm where it will not. For the
           rest - the dead, the plots, the schools, the police and the cells -
           what its count adds (count x unit()).
       The count is the one that keeps the need ahead at its opening, as
       before; only the choice between candidates reads the life. A first
       building is never stopped by it: the ranking always names the least,
       however far past the need it goes.
       ===================================================================== */

    /** The years of a building's life the served figure reads: LIFE_MONTHS in whole years. */
    static final int LIFE_YEARS = LIFE_MONTHS / 12;

    /**
     * The city's demand `months` from now over today's, for a building's life
     * (0.7.101): the city's own population trend over the last LIFE_MONTHS of
     * its record - or since its first month recorded, a younger city -
     * linear in the months as the businesses' growthFactor() is, never under
     * today's (star P2-2). Not growthFactor() itself: its trend is the last
     * TREND_WINDOW months' and it stops at the homes standing and on site,
     * which is the next months' future - over a life the market builds the
     * homes, and one flat year (the playtest's at month 2,000) or one boom
     * would read as the next twenty. The past life's growth, read off the
     * city's history (HistorySave.getPopulation()), for the next life's.
     */
    public static double lifeTrend(Game game, double months) {
        HistorySave h = game.getHistorySave();
        List<Long> people = h == null ? null : h.getPopulation();
        List<Integer> at = h == null ? null : h.getMonth();
        if (people == null || at == null || people.size() < 2 || at.size() != people.size()) return 1;
        int last = people.size() - 1, from = 0;
        for (int i = last; i >= 0; i--) {
            if (at.get(last) - at.get(i) >= LIFE_MONTHS) { from = i; break; }
        }
        double now = people.get(last), span = at.get(last) - at.get(from);
        if (!(now > 0) || !(span > 0)) return 1;
        double share = (now - people.get(from)) / span / now;
        return Math.max(1, 1 + share * Math.max(0, months));
    }

    /** An order of `count` of t over its life (0.7.101): its quote for the count, its ground at landValue() on the land left, and lifeFactor() months of running() for each. */
    public static double orderLife(Game game, BuildingsTemplate t, int count, double landLeft,
                                   Map<BuildingsTemplate, Long> site) {
        return orderLife(game, NewUnit.of(game, t), count, landLeft, site);
    }

    /** ...of one new building already resolved (QN1): its own running cost; its quote and ground are the template's. */
    public static double orderLife(Game game, NewUnit u, int count, double landLeft,
                                   Map<BuildingsTemplate, Long> site) {
        BuildingsTemplate t = u.template();
        return game.quoteBuild(t, count).total + landValue(game, t, t.getLandSqFt() * (double) count, landLeft + reserveHeldFor(game, t))
                + count * running(game, u, site) * lifeFactor(game);
    }

    /** ...`count` pavings' (0.7.101): their quote (Game.quotePave()), less the ground they free at landValue() on the land left, and lifeFactor() months of a paved road's running over a gravel road's for each; NaN with nothing to pave. */
    public static double pavingOrderLife(Game game, int count, double landLeft, Map<BuildingsTemplate, Long> site) {
        Game.BuildQuote q = game.quotePave(count);
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        if (q == null || from == null || to == null) return Double.NaN;
        return q.total - landValue(game, pavingFrees(game) * (double) count, landLeft)
                + count * (running(game, to, site) - running(game, from, site, false)) * lifeFactor(game);
    }

    /**
     * What a measure is short of, in its own unit, with these buildings
     * standing as well, at `p`'s demand (0.7.101): power and water the supply
     * NEEDS YOU's quarter in hand asks (the demand over NETWORK_YELLOW) less
     * the supply; the roads the trips over STRAINED of their capacity
     * (roadsOver()'s line, the unit a road's unit() is in); care the places
     * past what stands (since 0.7.106 the beds less the priced out,
     * carePlaces()). NaN for a measure not weighed so.
     */
    public static double shortfall(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p) {
        switch (m.kind()) {
            case GRID: {
                double[] sd = supplyDemand(game, m, added, p);
                return sd[1] - sd[0];
            }
            case POWER: case WATER: {
                double[] sd = supplyDemand(game, m, added, p);
                return sd[1] / (m.kind() == Kind.POWER ? CityNeeds.POWER_YELLOW : CityNeeds.NETWORK_YELLOW) - sd[0];
            }
            case ROADS: {
                InfrastructureManager r = roads(game, added, p.scale());
                return r.getStreetLoad() - InfrastructureManager.STRAINED * r.getStreetCapacity();
            }
            case HIGHWAYS:
                // The through trips with no highway to take them (FR7), the unit a highway's unit() is in.
                return roads(game, added, p.scale()).getThroughOverflow();
            case CARE: {
                double[] sd = supplyDemand(game, m, added, p);
                return sd[1] - sd[0];
            }
            default:
                return Double.NaN;
        }
    }

    /** Whether a measure's candidates are weighed for what they will serve over their lives (shortfall()): power, water, the roads (streets and highways) and care. */
    public static boolean weighsServed(Measure m) {
        switch (m.kind()) {
            case POWER: case WATER: case ROADS: case HIGHWAYS: case CARE: return true;
            default: return false;
        }
    }

    /**
     * What an order serves over its life, a month on average, in the
     * measure's unit (0.7.101): each of its LIFE_YEARS years read at its
     * middle month - from its opening, `p`'s months less the horizon
     * (BusinessInvestment.leadFor()), the demand today's times lifeTrend() with p's slack past it - as the shortfall
     * with what stands and is on site (`site`) less the shortfall with the
     * order too (both held at nothing), each year weighted by its twelve
     * months discounted as lifeFactor() discounts them. NaN for a measure not
     * weighed so (weighsServed()).
     */
    public static double servedOverLife(Game game, Measure m, Map<BuildingsTemplate, Long> site,
                                        Map<BuildingsTemplate, Long> order, Ahead p) {
        if (!weighsServed(m)) return Double.NaN;
        Map<BuildingsTemplate, Long> with = plus(site, order);
        double opens = BusinessInvestment.leadFor(p.months());
        double i = lifeRate(game) / 12;
        double served = 0, weights = 0;
        for (int y = 0; y < LIFE_YEARS; y++) {
            double w = 0;
            for (int j = 12 * y + 1; j <= 12 * y + 12; j++) w += i > 0 ? Math.pow(1 + i, -j) : 1;
            double months = opens + 12 * y + 6;
            Ahead at = new Ahead(months, lifeTrend(game, months), p.slack());
            double before = Math.max(0, shortfall(game, m, site, at));
            double after = Math.max(0, shortfall(game, m, with, at));
            served += w * Math.max(0, before - after);
            weights += w;
        }
        return weights > 0 ? served / weights : 0;
    }

    /** The figure from an order's life and what it serves (0.7.101): the life over what it will serve; with that not measured, or nothing, over what its count adds (count x unit). */
    public static double perServed(double life, double served, int count, double unit) {
        return served > 0 ? life / served : life / (Math.max(1, count) * unit);
    }

    /**
     * A candidate's informational cost per physical unit relieved (HW1).
     * The full plan's net present value ranks the advice. Reserve-only
     * capacity has no physical-relief denominator.
     */
    public static double lifePerServed(Game game, Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t,
                                       int count, double unit, Ahead p, double landLeft) {
        return WorksValue.of(game, m, site, t, count, landLeft).perUsed();
    }

    /* =====================================================================
       AN IRON FIELD THAT WILL NOT PAY BACK (0.7.101, batch P2)

       Jerus (decision B, 2026-10-10): the build advice says when an iron
       field will not pay back for a small town. Build's no-deposit page has
       sold the cheapest field holding iron since 0.7.64, and on the default
       world that was the 35-site founding field for about US$180M, on a bond
       when short; the test player has asked since 0.7.67 (batch M3b) whether
       the mines the field would carry pay its price back, and waits until
       they would. That test, moved here whole from the test player
       (LongPlaytest.ironWhenNeeded(), which reads it from here) so the page
       says what the player's rule says:
         - WHAT IT EARNS (fieldEarnings()): the mines the city could staff on
           it (Sector.staffableCount(), no more than its sites), each that
           would pay on the mining sector's own screen with the ones before
           it lifting - the first at BusinessInvestment.estimatedMonthlyProfit(),
           each after it selling at home what the planners' forecast of the
           mills' demand leaves (BusinessInvestment.forecast(), less the mines
           standing and on site) and the rest at the export price - the first
           that would not pay ending the count; their profit a month.
         - WHAT IT MUST EARN (fieldPayment()): the level payment that repays
           the field's price, in local money at the day's rate, over
           Game.BUILD_BOND_YEARS at the rate the market quotes for that much
           money (DebtManager.quoteRate()) - the funding page's bond, cash or
           not, since cash sunk in ground is cash the city does not lend.
       It pays back when the first is at least the second (ironPayback()).
       ===================================================================== */

    /** An iron field weighed (0.7.101): the mines the city could staff and work on it, their profit a month, and the month's payment that repays its price - it pays back when the profit is at least the payment. */
    public record Payback(int mines, double earns, double payment) {
        /** Whether the mines pay the field back. */
        public boolean pays() { return earns >= payment; }
    }

    /** Whether an offer holding iron would pay itself back in this city (fieldEarnings() against fieldPayment()) - since 0.7.142 by the best mine that fits each of its fields. */
    public static Payback ironPayback(Game game, LandParcel field) {
        double[] earned = fieldEarnings(game, field);
        return new Payback((int) earned[0], earned[1], fieldPayment(game, field));
    }

    /*
     * BY THE FIELDS IT HOLDS (0.7.142, batch I13; I5's note). fieldEarnings()
     * below values an offer as Iron Mines on all its sites, whatever their
     * type, where since 0.7.124 Iron Mining builds the mine that fits each
     * field by its type and size (ExtractionSector, BY TYPE AND SIZE): a
     * magnetite field takes a magnetite works, never an Iron Mine, and a
     * field of twenty free sites a large one if it scores best. So the
     * offer's fields are weighed one by one (CityLand.fieldSitesOn()), each
     * filled with the iron mines of its type in the plan's order - the best
     * earnings on its whole cost first (ExtractionSector.estimatedMonthlyProfit()
     * on BusinessInvestment.getCostOf() and its lorries), as many of it as
     * the field's sites hold, then the next on what is left (fieldMines());
     * and the mines are counted as fieldEarnings() counts them, each kind
     * over all the fields it fills up to what the sector could staff of it,
     * the room at home shared in turn, the first that would not pay ending
     * its kind. On an offer whose fields take Iron Mines alone (hematite,
     * none of twenty sites or more, or the large works scoring worse) it is
     * fieldEarnings()'s figure for its sites, to the bit.
     * Build's no-deposit page reads it (ironPayback()), and the test
     * player's iron rule (LongPlaytest.ironWhenNeeded()) - which neither
     * playtest reaches: no iron offer is listed when it looks, "found none
     * listed" every time, so neither trace moves.
     */

    /** A field of an offer and the mines that would fill it (0.7.142): its type, its sites, a kind of mine and how many of it. */
    public record FieldMines(Deposit.FieldType type, int sites, BuildingsTemplate mine, long count) { }

    /** Each iron field an offer holds, filled with the iron mines that fit it by type and size in the plan's order (above); a field no mine fits has a row with none. */
    public static List<FieldMines> fieldMines(Game game, LandParcel field) {
        List<FieldMines> out = new ArrayList<>();
        ham.citybuildersim.sectors.ExtractionSector mining = (ham.citybuildersim.sectors.ExtractionSector) game.getSectors().byKey(Sectors.MINING);
        if (mining == null || field == null) return out;
        BusinessInvestment plans = game.getBusinessInvestment();
        List<Deposit.FieldType> types = Resource.IRON.typesOf();
        for (int[] f : game.getCityLand().fieldSitesOn(field, Resource.IRON)) {
            Deposit.FieldType type = types.get(f[0]);
            List<BuildingsTemplate> kinds = new ArrayList<>();
            for (BuildingsTemplate t : game.getBuildingManager().getTemplatesBySector(Sectors.MINING)) {
                BuildingsTemplate.Site site = t.site();
                if (site != null && site.resource() == Resource.IRON && site.type() == type && site.sites() > 0) kinds.add(t);
            }
            java.util.Map<BuildingsTemplate, Double> score = new java.util.HashMap<>();
            for (BuildingsTemplate t : kinds) {
                double cost = plans.getCostOf(t, 1) + mining.fleetCapital(t);
                score.put(t, cost > 0 ? mining.estimatedMonthlyProfit(t, plans) / cost : Double.NEGATIVE_INFINITY);
            }
            kinds.sort((a, b) -> Double.compare(score.get(b), score.get(a)));
            int left = f[1];
            boolean any = false;
            for (BuildingsTemplate t : kinds) {
                int n = left / t.site().sites();
                if (n <= 0) continue;
                out.add(new FieldMines(type, f[1], t, n));
                left -= n * t.site().sites();
                any = true;
            }
            if (!any) out.add(new FieldMines(type, f[1], null, 0));
        }
        return out;
    }

    /** What an offer's iron fields would earn the city's mines a month (0.7.142; BY THE FIELDS IT HOLDS, above): {mines, their monthly profit}, fieldEarnings()'s count on fieldMines()' mines. */
    public static double[] fieldEarnings(Game game, LandParcel field) {
        Sector mining = game.getSectors().byKey(Sectors.MINING);
        if (mining == null || field == null) return new double[] { 0, 0 };
        BusinessInvestment plans = game.getBusinessInvestment();
        GoodsMarket ore = game.getEconomyManager().getMarkets().get(Good.IRON);
        double room = Math.max(0, plans.forecast(mining, ore) - mining.getCapacity(Good.IRON) - mining.getPipeline(Good.IRON));
        double abroad = Good.IRON.exportable() ? Math.max(0, ore.netExportPrice()) : 0;
        double perTonneAtHome = (ore.getLocalPrice() - abroad) * BusinessInvestment.operatingRateOf(mining.getOperatingRate());
        // Each kind of mine over all the fields it fills, in the order they first come: the staffing test is a kind's.
        Map<BuildingsTemplate, Long> kinds = new LinkedHashMap<>();
        for (FieldMines f : fieldMines(game, field)) if (f.mine() != null) kinds.merge(f.mine(), f.count(), Long::sum);
        long mines = 0;
        double monthly = 0, home = room;
        for (Map.Entry<BuildingsTemplate, Long> k : kinds.entrySet()) {
            double units = k.getKey().makes(Good.IRON);
            double first = plans.estimatedMonthlyProfit(Sectors.MINING, k.getKey());
            double homeFirst = Math.min(units, room);
            long staffable = mining.staffableCount(k.getKey(), k.getValue());
            for (long i = 0; i < staffable; i++) {
                double h = Math.max(0, Math.min(units, home));
                double profit = first + (h - homeFirst) * perTonneAtHome;
                if (!(profit > 0)) break;
                mines++;
                monthly += profit;
                home -= h;
            }
        }
        return new double[] { mines, monthly };
    }

    /**
     * What a field of `sites` iron sites would earn the city's mines a month
     * (0.7.67, M3b; moved from LongPlaytest in 0.7.101): {mines, their monthly
     * profit}. The mines are no more than the sites and than the mining
     * sector could staff (Sector.staffableCount()), and each counted would pay
     * on the sector's own screen (BusinessInvestment.estimatedMonthlyProfit())
     * with the ones before it lifting: the first is that figure, and each
     * after it sells at home what the planners' forecast of the mills' demand
     * leaves of the room (BusinessInvestment.forecast(), less the mines
     * standing and on site) and the rest at the export price, as
     * estimatedMakerProfit() splits it. The first that would not pay ends the
     * count.
     */
    public static double[] fieldEarnings(Game game, int sites) {
        // The Iron Mine by its permanent id (0.7.120, batch I3): a name is a label.
        BuildingsTemplate mine = game.getBuildingManager().getTemplate(ham.citybuildersim.sectors.Mining.IRON_MINE);
        Sector mining = game.getSectors().byKey(Sectors.MINING);
        if (mine == null || mining == null || sites <= 0) return new double[] { 0, 0 };
        BusinessInvestment plans = game.getBusinessInvestment();
        GoodsMarket ore = game.getEconomyManager().getMarkets().get(Good.IRON);
        double units = mine.makes(Good.IRON);
        double room = Math.max(0, plans.forecast(mining, ore) - mining.getCapacity(Good.IRON) - mining.getPipeline(Good.IRON));
        double first = plans.estimatedMonthlyProfit(Sectors.MINING, mine);
        double homeFirst = Math.min(units, room);
        double abroad = Good.IRON.exportable() ? Math.max(0, ore.netExportPrice()) : 0;
        double perTonneAtHome = (ore.getLocalPrice() - abroad) * BusinessInvestment.operatingRateOf(mining.getOperatingRate());
        int staffable = mining.staffableCount(mine, sites);
        int mines = 0;
        double monthly = 0;
        for (int i = 0; i < staffable; i++) {
            double home = Math.max(0, Math.min(units, room - i * units));
            double profit = first + (home - homeFirst) * perTonneAtHome;
            if (!(profit > 0)) break;
            mines++;
            monthly += profit;
        }
        return new double[] { mines, monthly };
    }

    /**
     * The month's payment that repays a field's price here over
     * Game.BUILD_BOND_YEARS (0.7.67, M3b; moved from LongPlaytest in 0.7.101):
     * the level payment at the rate the market quotes for that much money at
     * that term (DebtManager.quoteRate()) - the funding page's bond, whether
     * or not the cash would cover it, because cash sunk in ground is cash the
     * city does not lend or spend.
     */
    public static double fieldPayment(Game game, LandParcel field) {
        double price = field.localPrice(game.getForeignAccounts().getRate());
        int months = Game.BUILD_BOND_YEARS * 12;
        double monthlyRate = game.getDebtManager().quoteRate(price, months) / 12;
        return monthlyRate > 0 ? price * monthlyRate / (1 - Math.pow(1 + monthlyRate, -months)) : price / months;
    }

    /** Which way is worse for a measure's figure. */
    public static boolean higherWorse(Measure m) {
        switch (m.kind()) {
            case POWER: case WATER: case ROADS: case HIGHWAYS: case DEATH: case POLICE: case CELLS: case PORT: return true;
            case SCHOOL: return !m.school().isBasic();
            default: return false;
        }
    }

    /* =====================================================================
       A BERTH THAT PAYS BACK (FR3, 2026-10-19; docs/queue/FR3.md 5, the
       finding of spec-freight-and-infrastructure 9.5)

       "Ports earn nothing and are never advised": a berth was a pure cost,
       so the advice never offered one and automatic building never ordered
       one; only the test player (LongPlaytest.portsWhenNeeded(), its own
       rule) and the player did. The Ports division now charges the shippers
       and pays the city port dues (sectors.PortDivision), so a berth has a
       payback, and Ports.berthPaysBack() is it - the dues' own break-even,
       written once: the kind's uncovered tonnes (what could go by sea and no
       berth standing or on site takes) reach WORTH_A_BERTH / the dues dial of
       that berth's month.

       WHAT THE MEASURE IS. One Kind.PORT measure a kind of cargo
       (portMeasures()): it serves the PORTS terminals of its kind, its
       figure is the kind's uncovered tonnes a month, and its advice is the
       LARGEST terminal of the kind whose break-even the kind's whole sea
       trade reaches, once the uncovered tonnes pay back some terminal of it
       at all (Ports.payingTerminal(), the test player's own choice; SL1,
       2026-10-28, the size by the whole trade - chosen on the uncovered
       tonnes alone, Bulk Berths kept the dry bulk under the Bulk Terminal's
       break-even for ever): the Bulk Terminal when the dry bulk trade is
       past its break-even, else the Bulk Berth, and of it as many as the
       uncovered tonnes pay back, each counted after the ones before it, and
       one at least (Ports.orderCount(), payingCount(); it was one at a time,
       and a fast-growing city lagged by the months it took) - the
       berths on site counted against the next card. It has no
       NEEDS YOU row and no category ring: a berth is judged on the tonnes
       that cross the boundary now, not on a projection with slack, because
       the dues' break-even is the margin; and only where the city owns coast
       (Game.hasCoastFor()). It is NOT in suggest(), the overview's WHAT
       WOULD HELP MOST (a need-ranked list whose cards the screen reads a
       row from; FR4 has the screens): it is asked by suggestFor(), which the
       auto-builder (AutoBuilder.remit()) and the harness use.
       ===================================================================== */

    /** The port measures, one a kind of cargo, in Cargo order (FR3). */
    public static List<Measure> portMeasures() {
        List<Measure> out = new ArrayList<>();
        for (Ports.Cargo k : Ports.Cargo.values()) out.add(Measure.port(k));
        return out;
    }

    /** A port measure's uncovered tonnes a month with these buildings standing as well: the kind's carriable() less its berths standing and the berths in `added` (Ports.uncovered()). */
    static double portUncovered(Game game, Measure m, Map<BuildingsTemplate, Long> added) {
        Ports.Cargo k = m.cargo();
        double[] extra = new double[Ports.Cargo.values().length];
        for (Map.Entry<BuildingsTemplate, Long> e : added.entrySet()) {
            if (m.serves(e.getKey())) extra[k.ordinal()] += e.getValue() * Ports.berthMonth(e.getKey());
        }
        return game.getPorts().uncovered(k, Ports.berths(game.getBuildingManager()), extra);
    }

    /**
     * The terminal to order for a port measure, or null (FR3): with the kind's
     * uncovered tonnes (`added` standing too) paying back some terminal of it
     * but those in `skip` at the port dues dial, the largest of them whose
     * break-even the kind's whole sea trade reaches (Ports.payingTerminal(),
     * SL1), and only where the city owns coast for it
     * (Game.hasCoastFor(), since ET5 by the rule alone - Landmasses.RULE: home's shore, else a linked landmass, never an island no port links;
     * linking an island is the player's choice on the card).
     */
    public static BuildingsTemplate portTerminal(Game game, Measure m, Map<BuildingsTemplate, Long> added,
                                                 java.util.Set<BuildingsTemplate> skip) {
        double dial = game.getEconomyManager().getTaxPolicy().getPortDues();
        BuildingsTemplate t = Ports.payingTerminal(m.cargo(), game.getBuildingManager().getKnownTemplates(),
                portUncovered(game, m, added), game.getPorts().carriable(m.cargo()), dial, skip);
        return t != null && game.hasCoastFor(t, 1, Landmasses.RULE) ? t : null;
    }

    /**
     * A port measure's card: as many of the terminal portTerminal() chose as the uncovered tonnes pay back, each counted after
     * the ones before it, and one at least (SL1, Ports.orderCount(); it was one), or null for none (portTerminal()); the Suggestion's
     * need is null, the NEEDS YOU having no row for a berth. Its price, ground and figures are the count's, and the
     * auto-builder orders its count() (AutoBuilder.step()).
     */
    static Suggestion portSuggestion(Game game, Measure m, double cashLeft, double landLeft,
                                     java.util.Set<BuildingsTemplate> skip) {
        Map<BuildingsTemplate, Long> site = onSite(game, m);
        BuildingsTemplate t = portTerminal(game, m, site, skip);
        if (t == null) return null;
        double dial = game.getEconomyManager().getTaxPolicy().getPortDues();
        int n = Ports.orderCount(t, portUncovered(game, m, site), dial);
        Game.BuildQuote q = game.quoteBuildOn(t, n, Landmasses.RULE);
        Map<BuildingsTemplate, Long> order = plus(site, t, n);
        double after = figure(game, m, order);
        double unit = Ports.berthMonth(t), sq = t.getLandSqFt() * (double) n;
        boolean credit = q.total > cashLeft;
        return new Suggestion(null, m, t, n, q.total, figure(game, m, new LinkedHashMap<>()), figure(game, m, site), after,
                after, portTerminal(game, m, order, java.util.Collections.emptySet()) == null, credit, units(site),
                unit, unit > 0 ? q.total / (unit * n) : Double.NaN, NOW, q.months, sq, landValue(game, sq, landLeft),
                Math.max(0, sq - Math.max(0, landLeft)), null, Double.NaN, null,
                credit ? q.total - Math.max(0, cashLeft) : 0, false, null, null);
    }

    /* ----- A BERTH'S PAYBACK, FOR ITS CARD (FR4, 2026-10-20) ----- */

    /**
     * Where a berth stands on its payback (FR4): PAYS - the dues at the dial
     * cover its cost and more than its upkeep; SHORT - cargo of its kind
     * crosses and no berth takes it, but not enough of it (Ports.berthPaysBack());
     * COVERED - the berths standing and on site already take every tonne;
     * NO_DUES - the dial is at nothing, so no dues, so no payback; UPKEEP - the
     * uncovered tonnes reach the dues' break-even and the dues still do not
     * cover its upkeep; NO_CARGO - no cargo of its kind crosses the boundary
     * yet (Ports.carriable()).
     */
    public enum BerthState { PAYS, SHORT, COVERED, NO_DUES, UPKEEP, NO_CARGO }

    /**
     * One more of a terminal, weighed as the advice weighs one (FR4): the
     * kind's carriable and uncovered tonnes a month, the tonnes a month the
     * advice needs uncovered before the terminal pays back (needed; NaN with
     * no dues), the terminal's own month, the tonnes it would carry
     * (carried: the lesser of its month and the uncovered tonnes), the
     * dues a tonne it would pay the city at the dial and the dues a month on
     * the tonnes it carries, its upkeep (a berth's crews are Freight's, so
     * the city's running cost is its upkeep alone), its price at one and,
     * when it PAYS, the years it takes the dues over its upkeep to pay that
     * price back (NaN otherwise).
     */
    public record BerthPayback(BuildingsTemplate terminal, BerthState state, double carriable, double uncovered,
                               double needed, double berthMonth, double carried, double duesRate, double dues,
                               double upkeep, double price, double years) {

        /** Whether it pays back (state PAYS). */
        public boolean pays() { return state == BerthState.PAYS; }

        /** The tonnes a year it would carry. */
        public double carriedAYear() { return carried * 12; }
    }

    /**
     * A sea terminal's payback, as its card says it (FR4; null for any other
     * building). The verdict is the advice's own: the kind's uncovered tonnes
     * with the berths standing and on site counted (portUncovered()) against
     * Ports.berthPaysBack() at the dial in force, which portTerminal() asks.
     * The money is the port dues' own arithmetic for a berth that does not
     * stand yet: the dues rate that recovers its book value (cash and
     * materials) over PortDivision.PORT_LIFE_MONTHS at the city's rate, over
     * its month at PortDivision.DESIGN_USE (PortDivision.duesRateFor(), the
     * sum duesRateOf() strikes on the kind's berths standing), times the dial,
     * on the tonnes it would carry. Reads; changes nothing.
     */
    public static BerthPayback berthPayback(Game game, BuildingsTemplate t) {
        if (!Ports.isPort(t)) return null;
        Ports.Cargo k = t.berthCargo();
        Measure m = Measure.port(k);
        double carriable = game.getPorts().carriable(k);
        double uncovered = portUncovered(game, m, onSite(game, m));
        double dial = game.getEconomyManager().getTaxPolicy().getPortDues();
        double month = Ports.berthMonth(t);
        double needed = dial > 0 ? Ports.WORTH_A_BERTH / dial * month : Double.NaN;
        double carried = Math.min(month, uncovered);
        ham.citybuildersim.sectors.PortDivision port = game.getSectors().freight().portDivision();
        double book = t.getCashCost() + t.getConstructionMaterials() * game.getBuildingManager().getConstructionMaterialPrice();
        double rate = port.duesRateFor(book, month);
        double dues = carried * rate * dial;
        double upkeep = t.getUpkeep();
        double price = game.quoteBuild(t, 1).total;
        BerthState state;
        if (!(carriable > 0)) state = BerthState.NO_CARGO;
        else if (!(dial > 0)) state = BerthState.NO_DUES;
        else if (!(uncovered > 0)) state = BerthState.COVERED;
        else if (!Ports.berthPaysBack(uncovered, month, dial)) state = BerthState.SHORT;
        else if (!(dues - upkeep > 0)) state = BerthState.UPKEEP;
        else state = BerthState.PAYS;
        double years = state == BerthState.PAYS ? price / ((dues - upkeep) * 12) : Double.NaN;
        return new BerthPayback(t, state, carriable, uncovered, needed, month, carried, rate * dial, dues, upkeep, price, years);
    }

    /* =====================================================================
       THE ISLAND LINK (AL1, 2026-11-09)

       Jerus, 2026-10-31: "autobuild will build bridges and ports if sees that land is low and there is something
       unlinked (basically, if link is allowed with bridge, then it builds a highway bridge or road bridge ... if not it
       build a link via port)". linkFor() is that rule, written once for the automatic builder (AutoBuilder.link()) and the
       test player (LongPlaytest.linkIslands()) to read: what the city should order to link the island it most needs. It
       overturns ET5's "the AutoBuilder and the test player never build a port to link an island" and ET6-C's "the
       automatic builders never order a bridge". It places nothing, and leaves the card's pick of a bridge's far landmass
       as it found it.

       THE ISLAND is the unlinked landmass with the most buildable ground (the lower id on a tie) that no link is on its
       way to: a bridge on site to it, or a terminal standing or on site on it while some linked landmass has one too. In
       that order, the first island that can be linked is the answer, and its link is, in this order: a terminal ordered
       at home where one already stands on the island and none on linked ground (a port shared where one serves); else a
       bridge where BridgeSurvey finds a crossing - the Highway Bridge where the city has an Elevated Highway standing,
       else the Road Bridge; else the cheapest terminal on the island's own shore, and, where no linked landmass has a
       terminal, the cheapest at home beside it (Landmasses.closure(): an island's port links only beside a port on
       linked ground). No island can be linked: the first one's Link says so, kind NONE, with the words.

       NO RAIL BRIDGE: a Rail Bridge links as a Road Bridge does, and nothing in the model or on the map runs a train
       over one (ET6-C), so the rule never orders it. A TEMPLATE counts only when the city knows it (Game.knows(): the Rail
       and Road Bridge date from 1900, the Container Terminal from E5); the Build card has no check of its own yet, so the
       rule asks R3's. */

    /** How a link is made: a long bridge, sea terminals, or not at all (NONE: the Link says why). */
    public enum LinkKind { BRIDGE, PORT, NONE }

    /** One order of a link: the template, the landmass its order is for (a bridge's far landmass, or a terminal's `asked`: Landmasses.RULE for home, an id for an island's own shore), and its quote's total, thousands of US dollars. */
    public record LinkOrder(BuildingsTemplate template, int asked, double price) { }

    /**
     * What the city should order to link an island: the island's id, name and buildable ground (square feet), the kind,
     * the orders (none for NONE), the crossing a bridge is surveyed over (null otherwise), the orders' total price
     * (thousands of US dollars), the longest order's months (NaN where an order's site has no output) and, for NONE, why.
     */
    public record Link(int landmass, String name, double sqFt, LinkKind kind, List<LinkOrder> orders,
                       BridgeSurvey.Result survey, double price, double months, String why) {

        /** Its price a square foot of ground it links: the candidate's unit is the ground, not trips. */
        public double perSqFt() { return sqFt > 0 ? price / sqFt : Double.POSITIVE_INFINITY; }

        /** Whether a bridge is already on site to the island (FB13): the screens' link for it, a BRIDGE with no orders; linkFor() never returns one. */
        public boolean underWay() { return kind == LinkKind.BRIDGE && orders.isEmpty(); }

        /** The bridge a BRIDGE link orders (the Road Bridge, or the Highway Bridge where a highway stands), or null for any other link and for one under way. */
        public BuildingsTemplate bridge() { return kind == LinkKind.BRIDGE && !orders.isEmpty() ? orders.get(0).template() : null; }
    }

    /** One terminal order found, and its months. */
    private record Found(LinkOrder order, double months) { }

    /** The months a survey that found no crossing to an island stands before the rule surveys it again: such a survey searches out to 45 km, up to 1.7 s (ET6), and an island held for money or with no link at all is asked every month the ground is short: 12. */
    static final int NO_CROSSING_MONTHS = 12;

    /** A survey that found no crossing: the month it was made and its refusal in BridgeView's words. */
    private record NoCrossing(int month, String why) { }

    /** The surveys that found no crossing, by island, kept for each city's landmasses (a loaded or new city has its own; never saved, so a loaded city surveys once). */
    private static final Map<Landmasses, Map<Integer, NoCrossing>> NO_CROSSING = new java.util.WeakHashMap<>();

    /** How many crossings the rule has surveyed in this run (AL1): a harness reads that an island with none is not surveyed every month. */
    static int linkSurveys;

    /**
     * The link the city should order for the island it most needs, or null when nothing is unlinked (checked first and
     * alone: every generated world, and every city on one landmass, reads nothing else). Reads and quotes; changes nothing
     * but its memory of the surveys that found no crossing (NO_CROSSING_MONTHS).
     */
    public static Link linkFor(Game game) {
        Landmasses lms = game.getLandManager().getLandmasses();
        if (!lms.anyUnlinked()) return null;
        List<Landmasses.Row> islands = islandsToLink(game, lms);
        if (islands.isEmpty()) return null;
        Link first = null;
        for (Landmasses.Row island : islands) {
            Link l = linkOf(game, lms, island, false);
            if (l.kind() != LinkKind.NONE) return l;
            if (first == null) first = l;
        }
        return first;
    }

    /** The islands the rule would link, the one with the most buildable ground first (the lower id on a tie): unlinked, with no bridge on site to them and no terminal on them while linked ground has one. */
    private static List<Landmasses.Row> islandsToLink(Game game, Landmasses lms) {
        Bridges bridges = game.getBridges();
        List<Landmasses.Row> islands = new ArrayList<>();
        for (Landmasses.Row r : lms.rows()) {
            if (r.home() || r.linked() || !lms.unlinkedIds().contains(r.id())) continue;
            if (bridges.buildingTo(r.id())) continue;
            if (lms.portsAt(r.id()) > 0 && linkedTerminals(lms) > 0) continue;
            islands.add(r);
        }
        islands.sort((a, b) -> {
            int c = Double.compare(b.buildableSqFt(), a.buildableSqFt());
            return c != 0 ? c : Integer.compare(a.id(), b.id());
        });
        return islands;
    }

    /** The sea terminals standing or on site on the landmasses the city has linked. */
    private static long linkedTerminals(Landmasses lms) {
        long n = 0;
        for (int id : lms.linkedIds()) n += lms.portsAt(id);
        return n;
    }

    /**
     * The rule for one island. `screens` is a screen's read of it (FB13): the same rule, but it reads the rule's own memory of a survey that found no
     * crossing and never writes it, remembering what its own surveys found in SCREEN_NO_CROSSING, kept for the ground it found it on.
     */
    private static Link linkOf(Game game, Landmasses lms, Landmasses.Row island, boolean screens) {
        int id = island.id();
        boolean needHome = linkedTerminals(lms) == 0;
        String bridgeWhy = null, portWhy = null;
        // A terminal already on the island: only the one at home is wanting.
        if (lms.portsAt(id) > 0 && needHome) {
            Found home = cheapestTerminal(game, lms, Landmasses.RULE, -1);
            if (home != null) return portLink(island, List.of(home));
        }
        // A bridge where a crossing is allowed.
        BuildingsTemplate bridge = bridgeTemplate(game);
        Map<Integer, NoCrossing> none = screens ? NO_CROSSING.get(lms) : NO_CROSSING.computeIfAbsent(lms, k -> new java.util.HashMap<>());
        NoCrossing known = none == null ? null : none.get(id);
        CityLand land = game.getLandManager().getCityLand();
        long stamp = land == null ? 0 : land.stamp();
        ScreenNone seen = screens ? SCREEN_NO_CROSSING.computeIfAbsent(lms, k -> new java.util.HashMap<>()).get(id) : null;
        int age = known == null ? -1 : game.getMonth() - known.month();
        if (bridge == null) bridgeWhy = "No bridge is known yet.";
        // ...a survey that found no crossing stands NO_CROSSING_MONTHS (a month from before it, a city loaded over this one's, does not).
        else if (age >= 0 && age < NO_CROSSING_MONTHS) bridgeWhy = known.why();
        // ...and a screen's read goes by it first, so that it says what the rule will do: its own memory only where the rule has none, and then only for the ground it surveyed.
        else if (seen != null && seen.version() == lms.version() && seen.stamp() == stamp) bridgeWhy = seen.why();
        else {
            Bridges bridges = game.getBridges();
            int before = bridges.chosen(bridge.getId());
            try {
                game.chooseBridgeLandmass(bridge, id);
                if (screens) screenSurveys++;
                else linkSurveys++;
                BridgeSurvey.Result survey = game.bridgeSurvey(bridge);
                if (survey != null && survey.ok()) {
                    if (screens) SCREEN_NO_CROSSING.get(lms).remove(id);
                    else none.remove(id);
                    Game.BuildQuote q = game.quoteBuildOn(bridge, 1, Landmasses.RULE);
                    return new Link(id, island.name(), island.buildableSqFt(), LinkKind.BRIDGE,
                            List.of(new LinkOrder(bridge, id, q.total)), survey, q.total, q.months, null);
                }
                bridgeWhy = survey == null ? "No crossing could be surveyed." : BridgeView.refusal(survey, lms);
                if (screens) SCREEN_NO_CROSSING.get(lms).put(id, new ScreenNone(lms.version(), stamp, bridgeWhy));
                else none.put(id, new NoCrossing(game.getMonth(), bridgeWhy));
            } finally {
                if (before == Bridges.NONE_CHOSEN) bridges.clearChoice(bridge.getId());
                else game.chooseBridgeLandmass(bridge, before);
            }
        }
        // Else a port: on the island's own shore, and at home beside it where no linked landmass has one.
        List<Found> found = new ArrayList<>();
        if (lms.portsAt(id) == 0) {
            Found there = cheapestTerminal(game, lms, id, id);
            if (there == null) portWhy = lms.shorePlots(id) <= 0 ? island.name() + " has no shore the city owns" : "no room for a terminal on " + island.name();
            else found.add(there);
        }
        if (portWhy == null && needHome) {
            Found home = cheapestTerminal(game, lms, Landmasses.RULE, -1);
            if (home == null) portWhy = "home has no shore for the terminal it needs";
            else found.add(home);
        }
        if (portWhy == null && !found.isEmpty()) return portLink(island, found);
        if (portWhy == null) portWhy = "no terminal is wanted";
        return new Link(id, island.name(), island.buildableSqFt(), LinkKind.NONE, List.of(), null, 0, 0,
                bridgeWhy + " No port either: " + portWhy + ".");
    }

    private static Link portLink(Landmasses.Row island, List<Found> found) {
        List<LinkOrder> orders = new ArrayList<>();
        double price = 0, months = 0;
        for (Found f : found) {
            orders.add(f.order());
            price += f.order().price();
            months = Math.max(months, f.months());
        }
        return new Link(island.id(), island.name(), island.buildableSqFt(), LinkKind.PORT, orders, null, price, months, null);
    }

    /** The Highway Bridge where the city has an Elevated Highway standing, else the Road Bridge; null when the one wanted is not yet known (never the Rail Bridge). */
    private static BuildingsTemplate bridgeTemplate(Game game) {
        BuildingsTemplate road = null, highway = null;
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (!t.isBridge() || !game.knows(t)) continue;
            if (t.bridgeKind() == BridgeWorks.Kind.ROAD && road == null) road = t;
            if (t.bridgeKind() == BridgeWorks.Kind.HIGHWAY && highway == null) highway = t;
        }
        boolean highways = game.getBuildingManager().standingWhere(t -> t.getCategory() == BuildingType.INFRASTRUCTURE && t.isHighway()) > 0;
        return highways && highway != null ? highway : road;
    }

    /**
     * The cheapest sea terminal the city knows for a landmass: by the quote of one asked for `asked`, among those whose
     * order would stand where it is asked. `island` is an unlinked landmass's id, whose own room the terminal must fit
     * (LandManager.canAllocateOn(), as Game.buildStack() checks it); -1 for the rule's (home's), any landmass but none.
     */
    private static Found cheapestTerminal(Game game, Landmasses lms, int asked, int island) {
        Found best = null;
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (!Ports.isPort(t) || !game.knows(t)) continue;
            int where = lms.portLandmass(t.getId(), asked);
            if (island >= 0 ? where != island || !game.getLandManager().canAllocateOn(island, t.getLandSqFt()) : where == Landmasses.NO_LANDMASS) continue;
            Game.BuildQuote q = game.quoteBuildOn(t, 1, asked);
            if (best == null || q.total < best.order().price()) best = new Found(new LinkOrder(t, asked, q.total), q.months);
        }
        return best;
    }

    /* =====================================================================
       THE ISLAND LINK, AS THE SCREENS SAY IT (FB13, AL1-W)

       AL1 changed the model and the two builders; the screens kept saying "needs a port here" where a bridge is allowed, and the Build tab drew no
       card for the link. They now say what linkOf() says, island by island: a bridge where the rule would order one, a port where it would not.
       linkFor() is not what a screen reads: it places nothing but it does write (the survey that found no crossing is remembered for
       NO_CROSSING_MONTHS, and the rule's next survey waits on it), so a screen that read it would make the model depend on which screen was
       open. The screens run the same rule, reading the rule's own fresh memory first (so that they say what it will do) and keeping a memory
       of their own (SCREEN_NO_CROSSING) only where it has none, for the ground it surveyed; they keep what it says for the month, the
       landmasses' version, the land's stamp and the terminals and bridges on each island: one reading by a map that draws every frame.
       ===================================================================== */

    /** A survey that found no crossing as a screen's read of it remembers it: the refusal, and the landmasses' version and the land's stamp it was made on (it stands for that ground and no other, where the rule's stands NO_CROSSING_MONTHS). */
    private record ScreenNone(int version, long stamp, String why) { }

    /** The surveys that found no crossing as the screens remember them, apart from NO_CROSSING: what a screen has read never moves the rule's own memory, and the rule's own fresh memory is read before it. */
    private static final Map<Landmasses, Map<Integer, ScreenNone>> SCREEN_NO_CROSSING = new java.util.WeakHashMap<>();

    /** How many crossings the screens have surveyed in this run (FB13): a harness reads that a no-crossing island is surveyed once for a ground, not each month. */
    static int screenSurveys;

    /** What the screens last read, and what it was read against. */
    private record Shown(java.lang.ref.WeakReference<Landmasses> landmasses, int month, int version, long stamp, String fingerprint,
                         Map<Integer, Link> byIsland, Link advice) { }

    private static volatile Shown shownKept;

    /** How many times the screens' reading was made afresh in this run (FB13): a harness reads that a redraw with nothing new reads nothing. */
    static int shownReads;

    /**
     * What the rule says links one island, as the screens word it: the link linkOf() finds for it (BRIDGE, PORT, or NONE with the survey's
     * refusal and why no port), or - for an island a bridge is on site to - a BRIDGE with no orders (underWay()). Null for an island the rule
     * leaves alone (linked, home's, a terminal on it while linked ground has one) and for every city with nothing unlinked, which reads nothing.
     */
    public static Link islandLink(Game game, int island) {
        Shown s = shown(game);
        return s == null ? null : s.byIsland().get(island);
    }

    /** The link the Build tab's card shows: the one linkFor() would answer, the island it most needs; null when there is none. */
    public static Link linkShown(Game game) {
        Shown s = shown(game);
        return s == null ? null : s.advice();
    }

    private static Shown shown(Game game) {
        Landmasses lms = game.getLandManager().getLandmasses();
        if (!lms.anyUnlinked()) return null;
        Bridges bridges = game.getBridges();
        CityLand land = game.getLandManager().getCityLand();
        long stamp = land == null ? 0 : land.stamp();
        StringBuilder fingerprint = new StringBuilder().append(linkedTerminals(lms));
        for (int id : lms.unlinkedIds()) fingerprint.append(';').append(id).append(':').append(lms.portsAt(id)).append(':').append(bridges.buildingTo(id) ? 1 : 0);
        Shown kept = shownKept;
        if (kept != null && kept.landmasses().get() == lms && kept.month() == game.getMonth() && kept.version() == lms.version()
                && kept.stamp() == stamp && kept.fingerprint().equals(fingerprint.toString())) return kept;
        shownReads++;
        Map<Integer, Link> by = new java.util.LinkedHashMap<>();
        for (Landmasses.Row r : lms.rows()) {
            if (r.home() || r.linked() || !lms.unlinkedIds().contains(r.id()) || !bridges.buildingTo(r.id())) continue;
            by.put(r.id(), new Link(r.id(), r.name(), r.buildableSqFt(), LinkKind.BRIDGE, List.of(), null, 0, 0, null));
        }
        Link advice = null, first = null;
        for (Landmasses.Row island : islandsToLink(game, lms)) {
            Link l = linkOf(game, lms, island, true);
            by.put(island.id(), l);
            if (advice == null && l.kind() != LinkKind.NONE) advice = l;
            if (first == null) first = l;
        }
        Shown now = new Shown(new java.lang.ref.WeakReference<>(lms), game.getMonth(), lms.version(), stamp, fingerprint.toString(), by,
                advice != null ? advice : first);
        shownKept = now;
        return now;
    }
}
