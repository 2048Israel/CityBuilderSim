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
 * take NEEDS YOU's needs in its order (CityNeeds.biting()) - a school above
 * the ladder listed only for the students the city would both get and hire
 * (CityNeeds.wanted(), listsSchool()) - and keep those a city-built building
 * answers and what is on site does not already keep ahead; for each, of the
 * buildings that serve its measure (for the roads, any road or line), the
 * one with the lowest price per unit of staffed capacity with its ground
 * at what the city would pay to replace it (landValue(), the land office's
 * price) - preferring one that can keep the need ahead at all, and then one
 * whose whole count fits the ground the suggestions before it leave; the
 * count that keeps the need ahead at the demand it opens to, projected
 * the way the businesses project theirs (above the ladder, the students it
 * would get and hire by then, its feeder's graduates counted) and SLACK
 * past it - off NEEDS YOU's list there, and a served gauge at 100% of it -
 * after what is on site at its staffed capacity; on credit when its quote
 * is more than the cash the ones before leave, never cut to the cash; at
 * most three, one per need. Every judgement in it is named where it is made
 * below, and in the project's design notes for 0.7.24 and 0.7.51.
 */
public final class BuildAdvice {

    private BuildAdvice() { }

    /* =====================================================================
       THE CATEGORIES (0.7.24)

       The Build strip's fourteen, in its order: the five only the city
       builds, then the nine investors build too. Five were renamed as Jerus
       chose ("Yes, rename them"): Residential is Homes, Commercial Shops,
       Industrial Industry, Infrastructure Roads & transit, Services Offices -
       the Build category's label only; BuildingType, the sectors and the
       rail's Infrastructure tab keep theirs. Kept here, not in the window, so
       the harness can hold that every building sits in exactly one.
       ===================================================================== */

    /** The page Build opens on: the city's job, what would help most, and what the market builds. */
    public static final String OVERVIEW = "Overview";

    /**
     * One category of the strip.
     *
     * @param was        its label before 0.7.24, or null when it kept its own -
     *                   a caller still holding an old label lands in the same place
     * @param cityBuilds true for the five nobody builds but the city
     */
    public record Category(String name, String was, EnumSet<BuildingType> types, boolean cityBuilds) {
        /** A fresh set every call: EnumSet is mutable, and screens should not share one. */
        @Override public EnumSet<BuildingType> types() { return EnumSet.copyOf(types); }
    }

    /** The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. */
    public static final String UTILITIES = "Utilities", ROADS = "Roads & transit", HEALTHCARE = "Healthcare",
            EDUCATION = "Education", SAFETY = "Safety", HOMES = "Homes", SHOPS = "Shops", INDUSTRY = "Industry",
            OFFICES = "Offices", FARMS = "Farms", RAIL = "Rail", VEHICLES = "Vehicles",
            LUXURY = "Luxury shops", RESTAURANTS = "Restaurants";

    /** The strip, in its order: the city's five, then the market's nine. */
    public static List<Category> categories() {
        List<Category> out = new ArrayList<>();
        out.add(new Category(UTILITIES,   null, EnumSet.of(BuildingType.ELECTRICITY, BuildingType.WATER), true));
        out.add(new Category(ROADS,       "Infrastructure", EnumSet.of(BuildingType.INFRASTRUCTURE), true));
        out.add(new Category(HEALTHCARE,  null, EnumSet.of(BuildingType.HEALTHCARE), true));
        out.add(new Category(EDUCATION,   null, EnumSet.of(BuildingType.EDUCATION), true));
        out.add(new Category(SAFETY,      null, EnumSet.of(BuildingType.SAFETY), true));
        out.add(new Category(HOMES,       "Residential", EnumSet.of(BuildingType.RESIDENTIAL), false));
        out.add(new Category(SHOPS,       "Commercial", EnumSet.of(BuildingType.COMMERCIAL), false));
        out.add(new Category(INDUSTRY,    "Industrial", EnumSet.of(BuildingType.INDUSTRIAL,
                BuildingType.HEAVY_INDUSTRY, BuildingType.MINING, BuildingType.CONSTRUCTION), false));
        out.add(new Category(OFFICES,     "Services", EnumSet.of(BuildingType.BUSINESS_SERVICES), false));
        out.add(new Category(FARMS,       null, EnumSet.of(BuildingType.AGRICULTURE), false));
        out.add(new Category(RAIL,        null, EnumSet.of(BuildingType.RAIL), false));
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

    /** The category a kind of building is in. */
    public static Category categoryOf(BuildingType type) {
        for (Category c : categories()) if (c.types.contains(type)) return c;
        return null;
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
       death care; Education: each level; Safety: police and cells; Roads &
       transit: road capacity and transit; Utilities: power and water. PLOTS
       has no ring - it is the burial plots' own NEEDS YOU row, which the
       death care ring's buildings answer.
       ===================================================================== */

    public enum Kind { POWER, WATER, ROADS, TRANSIT, CARE, DEATH, PLOTS, SCHOOL, POLICE, CELLS }

    /** One measure: a kind, and the care type or the school it is about. */
    public record Measure(Kind kind, CareType care, EducationType school) {

        public static Measure of(Kind kind) { return new Measure(kind, CareType.NONE, EducationType.NONE); }
        public static Measure care(CareType care) { return new Measure(Kind.CARE, care, EducationType.NONE); }
        public static Measure school(EducationType school) { return new Measure(Kind.SCHOOL, CareType.NONE, school); }

        /** Whether this building serves the measure - the buildings its ring shows. */
        public boolean serves(BuildingsTemplate t) {
            if (t == null) return false;
            switch (kind) {
                case POWER:   return t.getCategory() == BuildingType.ELECTRICITY;
                case WATER:   return t.getCategory() == BuildingType.WATER;
                case ROADS:   return t.getCategory() == BuildingType.INFRASTRUCTURE && t.getCapacity() > 0;
                case TRANSIT: return t.getCategory() == BuildingType.INFRASTRUCTURE && t.getTransitCapacity() > 0;
                case CARE:    return t.getCare() == care;
                case DEATH:   return t.getCare() == CareType.BURIAL || t.getCare() == CareType.CREMATION;
                case PLOTS:   return t.getCare() == CareType.BURIAL;
                case SCHOOL:  return t.getTeaches() == school;
                case POLICE:  return t.getSafety() == SafetyType.POLICE;
                case CELLS:   return t.getSafety() == SafetyType.PRISON;
                default:      return false;
            }
        }

        /** The category its buildings are in. */
        public String category() {
            switch (kind) {
                case POWER: case WATER:        return UTILITIES;
                case ROADS: case TRANSIT:      return ROADS;
                case CARE: case DEATH: case PLOTS: return HEALTHCARE;
                case SCHOOL:                   return EDUCATION;
                default:                       return SAFETY;
            }
        }

        /** Its name, as a ring and a heading say it. */
        public String label() {
            switch (kind) {
                case POWER:   return "Power";
                case WATER:   return "Water";
                case ROADS:   return "Road capacity";
                case TRANSIT: return "Transit";
                case CARE:    return care == CareType.GENERAL ? "General care"
                                   : care == CareType.CHILDCARE ? "Childcare" : "Senior care";
                case DEATH:   return "Death care";
                case PLOTS:   return "Burial plots";
                case SCHOOL:  return school.getLabel();
                case POLICE:  return "Police";
                default:      return "Cells";
            }
        }

        /** True for a figure that is a load - the share of a network's capacity in use. */
        public boolean isLoad() { return kind == Kind.POWER || kind == Kind.WATER || kind == Kind.ROADS; }
    }

    /** The measures a city category's page opens on, in its ring order; empty for a market category. */
    public static List<Measure> measuresOf(String category) {
        List<Measure> out = new ArrayList<>();
        Category c = category(category);
        if (c == null || !c.cityBuilds()) return out;
        switch (c.name()) {
            case UTILITIES:
                out.add(Measure.of(Kind.POWER));
                out.add(Measure.of(Kind.WATER));
                break;
            case ROADS:
                out.add(Measure.of(Kind.ROADS));
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
        double posts = 0, staffed = 0;
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
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
        double unfilled = 0;
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
            if (n == 0) continue;
            double f = fill != null && job.ordinal() < fill.length ? fill[job.ordinal()] : 1;
            unfilled += n * Math.max(0, 1 - f);
        }
        return unfilled;
    }

    /** Whether a building has any posts at all - a road has none, and its card says so instead of drawing a staff bar. */
    public static boolean hasPosts(BuildingsTemplate t) {
        for (JobType job : JobType.values()) if (t.getJobs(job) > 0) return true;
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
        if (!m.serves(t) && !(m.kind() == Kind.ROADS && t.getCategory() == BuildingType.INFRASTRUCTURE)) return 0;
        double[] fill = game.getPopulationManager().getJobFillRate();
        switch (m.kind()) {
            case POWER:
                return t.getProduction1() * staffing(t, fill);
            case WATER: {
                /*
                 * THE WATER UNIT (0.7.59, batch J2): a fresh plant adds no
                 * more than the fresh water left under the city's limit
                 * (UtilitiesHandler.getFreshHeadroom()), so a capped city is
                 * offered desalination; a desalination plant adds its own
                 * where the city owns sea, and nothing where it cannot stand.
                 */
                double treats = t.getProduction1() * staffing(t, fill);
                if (t.isSeaWater()) return game.hasCoastFor(t, 1) ? treats : 0;
                return Math.min(treats, game.getServicesManager().getUtilitiesHandler().getFreshHeadroom());
            }
            case ROADS: {
                Map<BuildingsTemplate, Integer> base = onSite(game, m);
                return roadsOver(game, base) - roadsOver(game, plus(base, t, 1));
            }
            case TRANSIT:
                return t.getTransitCapacity();
            case DEATH: case PLOTS:
                return t.getCare() == CareType.BURIAL ? t.getCapacity() : t.getCapacity() * staffing(t, fill);
            default:
                return t.getCapacity() * staffing(t, fill);
        }
    }

    /** The same, fully staffed: what the building is built for. */
    public static double built(Game game, Measure m, BuildingsTemplate t) {
        switch (m.kind()) {
            case POWER: case WATER: return t.getProduction1();
            case ROADS:             return t.getCapacity();
            case TRANSIT:           return t.getTransitCapacity();
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

    /** Every unit on site of the buildings that serve a measure, for anybody's order. */
    public static Map<BuildingsTemplate, Integer> onSite(Game game, Measure m) {
        Map<BuildingsTemplate, Integer> out = new LinkedHashMap<>();
        for (BuildingsStacks site : game.getBuildingManager().getStacksUnderConstruction()) {
            BuildingsTemplate t = site.getBuilding();
            boolean counts = m.kind() == Kind.ROADS
                    ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
            if (counts && site.getUnderConstruction() > 0) out.merge(t, site.getUnderConstruction(), Integer::sum);
        }
        return out;
    }

    /** Units on site, all told. */
    public static int units(Map<BuildingsTemplate, Integer> added) {
        int n = 0;
        for (int v : added.values()) n += v;
        return n;
    }

    /** A copy with n more of t. */
    public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, BuildingsTemplate t, int n) {
        Map<BuildingsTemplate, Integer> out = new LinkedHashMap<>(base);
        if (n != 0) out.merge(t, n, Integer::sum);
        return out;
    }

    /** ...and with every quantity of another added. */
    public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, Map<BuildingsTemplate, Integer> more) {
        Map<BuildingsTemplate, Integer> out = new LinkedHashMap<>(base);
        for (Map.Entry<BuildingsTemplate, Integer> e : more.entrySet()) out.merge(e.getKey(), e.getValue(), Integer::sum);
        return out;
    }

    /** The measure's figure with these buildings standing as well. */
    public static double figure(Game game, Measure m, Map<BuildingsTemplate, Integer> added) {
        return figure(game, m, added, NOW);
    }

    /** ...against the demand `p` says (0.7.51): today's times p.scale() - projected and padded - on the demand side only. */
    public static double figure(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        BuildingManager bm = game.getBuildingManager();
        double k = p.scale();
        switch (m.kind()) {
            case POWER:
            case WATER: {
                // UtilitiesHandler: production = (built + base) x the utilities'
                // pooled fill, Σ fill x jobs / Σ jobs over both utilities' posts.
                UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
                boolean power = m.kind() == Kind.POWER;
                long[] je = bm.getJobArrayPerCategory(BuildingType.ELECTRICITY);
                long[] jw = bm.getJobArrayPerCategory(BuildingType.WATER);
                double jobs = 0;
                for (int i = 0; i < je.length; i++) jobs += je[i] + jw[i];
                double filled = u.getAverageUtilityFill() * jobs;
                double base = power ? u.getBaseProduction() : u.getBaseWaterProduction();
                // ...and water's two parts, the fresh held to the city's limit (0.7.59).
                double fresh = u.getFreshNameplate(), desal = u.getDesalNameplate();
                double demand = k * (power ? u.getConsumption() : u.getWaterConsumption());
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    BuildingsTemplate t = e.getKey();
                    int n = e.getValue();
                    if (t.getCategory() == (power ? BuildingType.ELECTRICITY : BuildingType.WATER)) {
                        base += n * t.getProduction1();
                    }
                    if (t.isFreshWater()) fresh += n * t.getProduction1();
                    if (t.isSeaWater()) desal += n * t.getProduction1();
                    demand += n * (power ? t.getElectricityConsumption() : t.getWaterConsumption());
                    if (t.getCategory() == BuildingType.ELECTRICITY || t.getCategory() == BuildingType.WATER) {
                        for (JobType job : JobType.values()) {
                            int posts = t.getJobs(job);
                            if (posts == 0) continue;
                            jobs += (double) n * posts;
                            filled += (double) n * posts * (job.ordinal() < fill.length ? fill[job.ordinal()] : 1);
                        }
                    }
                }
                double average = jobs > 0 ? filled / jobs : 1;
                double supply = average == 0 ? (power ? 10000 : 8000) : base * average;
                // Past the fresh water limit the fresh plants treat the cap (UtilitiesHandler,
                // THE FRESH WATER LIMIT); short of it, the line above to the bit.
                if (!power && fresh * average > u.getFreshCap()) {
                    supply = UtilitiesHandler.waterOutput(fresh, desal, average, u.getFreshCap());
                }
                return supply > 0 ? demand / supply : Double.POSITIVE_INFINITY;
            }
            case ROADS:
                return roads(game, added, k).getUtilisation();
            case TRANSIT:
                return roads(game, added, k).getTransitCover();
            case CARE: {
                double cap = bm.getStaffedCareCapacity(m.care(), fill);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getCare() == m.care()) cap += e.getValue() * (long) e.getKey().getCapacity() * staffing(e.getKey(), fill);
                }
                return Health.coverageOf(cap, k * m.care().populationServed(game.getCohorts()));
            }
            case DEATH:
                return unburiedNext(game, added, k);
            case PLOTS: {
                Healthcare h = game.getHealthcare();
                double plots = bm.getCareCapacity(CareType.BURIAL);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
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
            default:
                return 0;
        }
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
    public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added) {
        return supplyDemand(game, m, added, NOW);
    }

    /** ...against the demand `p` says (0.7.51), as figure() reads it: the supply side unchanged. */
    public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        BuildingManager bm = game.getBuildingManager();
        double k = p.scale();
        switch (m.kind()) {
            case POWER: case WATER: {
                double load = figure(game, m, added, p);
                UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
                double demand = k * (m.kind() == Kind.POWER ? u.getConsumption() : u.getWaterConsumption());
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    demand += e.getValue() * (m.kind() == Kind.POWER
                            ? e.getKey().getElectricityConsumption() : e.getKey().getWaterConsumption());
                }
                return new double[] {load > 0 ? demand / load : 0, demand};
            }
            case ROADS: {
                InfrastructureManager r = roads(game, added, k);
                return new double[] {r.getCapacity(), r.getEffectiveLoad()};
            }
            case TRANSIT: {
                InfrastructureManager r = roads(game, added, k);
                return new double[] {r.getUsableTransit(), r.getLoad(Traffic.COMMUTERS)};
            }
            case CARE: {
                double cap = bm.getStaffedCareCapacity(m.care(), fill);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getCare() == m.care()) cap += e.getValue() * (long) e.getKey().getCapacity() * staffing(e.getKey(), fill);
                }
                return new double[] {cap, k * m.care().populationServed(game.getCohorts())};
            }
            case DEATH: {
                Healthcare h = game.getHealthcare();
                double toHandle = k * h.getDeaths() + h.getUnburied();
                double waiting = figure(game, m, added, p);
                return new double[] {toHandle - waiting, toHandle};
            }
            case PLOTS: {
                Healthcare h = game.getHealthcare();
                double plots = bm.getCareCapacity(CareType.BURIAL);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getCare() == CareType.BURIAL) plots += e.getValue() * (double) e.getKey().getCapacity();
                }
                return new double[] {Healthcare.plotsRemaining(plots, h.getPlotsUsed()), k * h.getBurials() * CityNeeds.PLOTS_YELLOW};
            }
            case SCHOOL: {
                double[] places = bm.getStaffedEducationPlaces(fill);
                double seats = places[m.school().ordinal()];
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getTeaches() == m.school()) seats += e.getValue() * (long) e.getKey().getCapacity() * staffing(e.getKey(), fill);
                }
                return new double[] {seats, schoolNeed(game, m.school(), p)};
            }
            case POLICE:
                return new double[] {officers(game, added),
                        k * game.getCrime().getPopulation() * Crime.FULL_OFFICERS_PER_100K / 100_000.0};
            case CELLS:
                return new double[] {cells(game, added), k * game.getCrime().getCaught() * Crime.SENTENCE_MONTHS};
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

    /** The figure as a share of what is needed, 0 to 1 - supply over demand, held to it - for a ring and the order bar's stacked bar. */
    public static double cover(Game game, Measure m, Map<BuildingsTemplate, Integer> added) {
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
            case POWER: case WATER: case ROADS: case TRANSIT: case CARE: case SCHOOL: return true;
            default: return false;
        }
    }

    /** What the measure serves with these buildings standing as well (0.7.41): supply over demand, unclamped; NaN for a measure that is not served (isServed()). */
    public static double served(Game game, Measure m, Map<BuildingsTemplate, Integer> added) {
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
    public static CityNeeds.Served verdict(Game game, Measure m, Map<BuildingsTemplate, Integer> added) {
        if (!isServed(m)) return null;
        double share = served(game, m, added);
        switch (m.kind()) {
            case POWER:   return CityNeeds.verdict(CityNeeds.Kind.POWER, CareType.NONE, share);
            case WATER:   return CityNeeds.verdict(CityNeeds.Kind.WATER, CareType.NONE, share);
            case ROADS:   return CityNeeds.verdict(CityNeeds.Kind.ROADS, CareType.NONE, share);
            case CARE:    return CityNeeds.verdict(CityNeeds.Kind.CARE, m.care(), share);
            case SCHOOL:
                if (m.school().isBasic()) return CityNeeds.verdict(CityNeeds.Kind.BASIC_SCHOOLS, CareType.NONE, share);
                double[] sd = supplyDemand(game, m, added);
                if (!CityNeeds.listsSchool(game, m.school(), sd[1], sd[0])) return CityNeeds.unjudged(share);
                return CityNeeds.verdict(CityNeeds.Kind.HIGHER_SCHOOL, CareType.NONE, share);
            default:      return CityNeeds.unjudged(share);
        }
    }

    /** A served measure's two lines in served terms (0.7.41), {off NEEDS YOU's list past this, red at or under this}; null for transit and a measure not served. */
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
        return verdict(game, s.measure(), plus(onSite(game, s.measure()), s.template(), s.count()));
    }

    /** The road network with these buildings standing: InfrastructureManager.with(), the model's own curve. */
    static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added) {
        return roads(game, added, 1);
    }

    /** ...with the city's own traffic times k (0.7.51): the load and every stream grown alike, then the buildings' own as before. */
    static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added, double k) {
        InfrastructureManager now = game.getInfrastructureManager();
        double capacity = 0, highway = 0, transit = 0, load = (k - 1) * now.getLoad();
        double[] streams = new double[Traffic.values().length];
        for (Traffic s : Traffic.values()) streams[s.ordinal()] = (k - 1) * now.getLoad(s);
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            int n = e.getValue();
            if (t.getCategory() == BuildingType.INFRASTRUCTURE) {
                capacity += n * (double) t.getCapacity();
                highway += n * (long) t.getCapacity() * t.getFreightGrade();
                transit += n * t.getTransitCapacity();
            }
            load += n * t.getRoadLoad();
            for (Traffic s : Traffic.values()) streams[s.ordinal()] += n * t.loadOf(s);
        }
        return now.with(capacity, highway, transit, load, streams);
    }

    /**
     * Trips over the line: what the road carries past STRAINED of its
     * capacity, the line NEEDS YOU lists the roads at. What a road or a line
     * relieves is how much of this one building takes away - more capacity,
     * a grade-separated share for freight, riders off the road - which is
     * how roads and transit are compared at all when capacity has its own
     * streams.
     */
    static double roadsOver(Game game, Map<BuildingsTemplate, Integer> added) {
        InfrastructureManager r = roads(game, added);
        return r.getEffectiveLoad() - InfrastructureManager.STRAINED * r.getCapacity();
    }

    /** The dead next month at today's deaths: those waiting and those dying, less what the free plots and the ovens take (Healthcare.settleDeaths()'s arithmetic). */
    static double unburiedNext(Game game, Map<BuildingsTemplate, Integer> added) {
        return unburiedNext(game, added, 1);
    }

    /** ...with k times today's deaths (0.7.51): the unburied are a stock, and stay as they are. */
    static double unburiedNext(Game game, Map<BuildingsTemplate, Integer> added, double k) {
        Healthcare h = game.getHealthcare();
        BuildingManager bm = game.getBuildingManager();
        double[] fill = game.getPopulationManager().getJobFillRate();
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, fill);
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            if (t.getCare() == CareType.BURIAL) plots += e.getValue() * (double) t.getCapacity();
            if (t.getCare() == CareType.CREMATION) ovens += e.getValue() * (long) t.getCapacity() * staffing(t, fill);
        }
        double free = Healthcare.plotsRemaining(plots, h.getPlotsUsed());
        double deaths = k * h.getDeaths();
        double waiting = Math.max(0, deaths + h.getUnburied() - free - Math.max(0, ovens));
        return Math.min(waiting, deaths * Healthcare.MAX_BACKLOG_MONTHS);
    }

    /** A school's figure: a basic stage's coverage (Education's own cover, places over the children it serves), or above the ladder who would come and be hired over the seats (0.7.51; who would come until then). */
    static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added) {
        return school(game, type, added, NOW);
    }

    /** ...against the people `p` says it serves (schoolNeed()). */
    static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added, Ahead p) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double[] places = game.getBuildingManager().getStaffedEducationPlaces(fill);
        double seats = places[type.ordinal()];
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            if (e.getKey().getTeaches() == type) seats += e.getValue() * (long) e.getKey().getCapacity() * staffing(e.getKey(), fill);
        }
        double need = schoolNeed(game, type, p);
        if (type.isBasic()) {
            if (need <= 0) return seats > 0 ? 1 : 0;
            return Math.max(0, Math.min(1, seats / need));
        }
        return need / Math.max(seats, 1);
    }

    static double officers(Game game, Map<BuildingsTemplate, Integer> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double n = game.getBuildingManager().getStaffedSafetyCapacity(SafetyType.POLICE, fill);
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            if (e.getKey().getSafety() == SafetyType.POLICE) n += e.getValue() * (long) e.getKey().getCapacity() * staffing(e.getKey(), fill);
        }
        return n;
    }

    static double cells(Game game, Map<BuildingsTemplate, Integer> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double n = game.getBuildingManager().getStaffedSafetyCapacity(SafetyType.PRISON, fill);
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            if (e.getKey().getSafety() == SafetyType.PRISON) n += e.getValue() * (long) e.getKey().getCapacity() * staffing(e.getKey(), fill);
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
            case POWER: case WATER: return figure < CityNeeds.NETWORK_YELLOW;
            case ROADS:   return figure < InfrastructureManager.STRAINED;
            case CARE:    return figure > (m.care() == CareType.GENERAL ? CityNeeds.GENERAL_YELLOW : CityNeeds.OTHER_CARE_YELLOW);
            case DEATH:   return !(figure > 0);
            case PLOTS:   return figure > CityNeeds.PLOTS_YELLOW;
            case SCHOOL:  return m.school().isBasic() ? figure > CityNeeds.SCHOOLS_YELLOW : figure < CityNeeds.SEATS_YELLOW;
            case POLICE:  return figure < CityNeeds.CRIME_YELLOW;
            case CELLS:   return figure < CityNeeds.CELLS_YELLOW;
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
    public static double[] needs(Map<BuildingsTemplate, Integer> order) {
        double[] out = new double[JobType.values().length + 1];
        for (Map.Entry<BuildingsTemplate, Integer> e : order.entrySet()) {
            for (JobType job : JobType.values()) out[job.ordinal()] += (double) e.getValue() * e.getKey().getJobs(job);
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
    public static double quoteTotal(Game game, Map<BuildingsTemplate, Integer> order) {
        double total = 0;
        for (Map.Entry<BuildingsTemplate, Integer> e : order.entrySet()) total += game.quoteBuild(e.getKey(), e.getValue()).total;
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

    /** The slack an order is sized with past its projection: the businesses' own headroom (BusinessInvestment.TARGET_HEADROOM). */
    public static final double SLACK = BusinessInvestment.TARGET_HEADROOM;

    /** Months past an order's opening it is sized for: the businesses' (BusinessInvestment.PLANNING_HORIZON); the opening's wait is held to their BusinessInvestment.MAX_ORDER_MONTHS. */
    public static final double HORIZON = BusinessInvestment.PLANNING_HORIZON;

    /**
     * The demand an order is sized against (0.7.51): today's, `months`
     * ahead grown by k (BusinessInvestment.growthFactor()), and padded by
     * `slack`.
     */
    public record Ahead(double months, double k, double slack) {
        /** What today's demand is multiplied by. */
        public double scale() { return k * (1 + slack); }
    }

    /** Today's demand, no slack: what NEEDS YOU reads. */
    public static final Ahead NOW = new Ahead(0, 1, 0);

    /**
     * The demand an order that opens after `lead` months is sized to: the
     * wait, held to the businesses' MAX_ORDER_MONTHS (a stalled one, NaN,
     * counts as none), plus HORIZON - the businesses' growthFactor() over
     * those months - and SLACK past it.
     */
    public static Ahead opening(Game game, double lead) {
        double wait = Double.isNaN(lead) ? 0 : Math.max(0, Math.min(lead, BusinessInvestment.MAX_ORDER_MONTHS));
        double months = wait + HORIZON;
        return new Ahead(months, game.getBusinessInvestment().growthFactor(months), SLACK);
    }

    /**
     * Whether these buildings keep the measure ahead at `p`'s demand: off
     * NEEDS YOU's list there (clear()), and a served measure - not transit,
     * which has no line - at 100% of it or more. With SLACK in p that is
     * green, not just off the list.
     */
    public static boolean ahead(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p) {
        if (!clear(m, figure(game, m, added, p))) return false;
        if (!isServed(m) || m.kind() == Kind.TRANSIT) return true;
        double[] sd = supplyDemand(game, m, added, p);
        return CityNeeds.servedShare(sd[0], sd[1]) >= 1;
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

    /** Why the next building in the ranking lost to the one suggested: dearer a unit with its land, it cannot keep the need ahead, or its count needs more land than is left. */
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
     * @param afterAtOpening ...at the demand it opens to, projected
     *                    (ahead's months and k) without the slack
     * @param closes      false when no count of this building keeps the need
     *                    ahead (crime with every officer it can use, transit
     *                    past the share of commuters who will ride): then
     *                    count is where it stops helping
     * @param needsCredit its quote is more than the cash the suggestions
     *                    before it leave
     * @param onSite      units on site that already serve the measure
     * @param unit        what one serves at today's staffing
     * @param pricePerUnit its quote for one and its ground (landValue(), on
     *                    the land the ones before leave), over unit
     * @param ahead       the demand it is sized to (opening(lead))
     * @param lead        the city order's own wait, Game.quoteBuild()'s months
     *                    for the count at today's demand with the slack
     * @param landSqFt    the ground the count stands on
     * @param landValue   ...at landValue(), on the land the ones before leave
     * @param landShort   square feet past the land the ones before leave, or 0
     * @param runnerUp    the next building in the ranking, or null for none
     * @param runnerUpPer ...its price a unit with its land, NaN for none
     * @param runnerUpLost ...and why it lost, null for none
     * @param credit      the part of its quote the cash the ones before leave
     *                    does not cover: 0 unless needsCredit
     */
    public record Suggestion(CityNeeds.Need need, Measure measure, BuildingsTemplate template,
                             int count, double price, double before, double whenOnSite, double after,
                             double afterAtOpening, boolean closes, boolean needsCredit, int onSite,
                             double unit, double pricePerUnit, Ahead ahead, double lead,
                             double landSqFt, double landValue, double landShort,
                             BuildingsTemplate runnerUp, double runnerUpPer, Lost runnerUpLost, double credit) { }

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
        for (CityNeeds.Need need : CityNeeds.biting(all)) {
            if (out.size() >= MAX_SUGGESTIONS) break;
            if (!need.cityBuilds()) continue;
            Measure m = measureOf(need);
            if (m == null) continue;
            Suggestion s = suggestFor(game, need, m, cashLeft, landLeft);
            if (s == null) continue;
            out.add(s);
            cashLeft = Math.max(0, cashLeft - s.price());
            landLeft = Math.max(0, landLeft - s.landSqFt());
        }
        return out;
    }

    /** The suggestions as one run, as "Build all three" places it: each card's building and count in their order, two of one building added together. */
    public static LinkedHashMap<BuildingsTemplate, Integer> run(List<Suggestion> advice) {
        LinkedHashMap<BuildingsTemplate, Integer> run = new LinkedHashMap<>();
        for (Suggestion s : advice) run.merge(s.template(), s.count(), Integer::sum);
        return run;
    }

    /** One building weighed for a need: its count at its projection, and where it ranks. */
    private record Weighed(BuildingsTemplate t, int tier, double per, double unit, int count,
                           boolean closes, boolean fits, Ahead ahead, double lead) { }

    /** One need's suggestion, or null when what is on site already keeps it ahead or no building can help. */
    static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft) {
        Map<BuildingsTemplate, Integer> site = onSite(game, m);
        double whenOnSite = figure(game, m, site);
        // JUDGEMENT: what is on site already keeps it ahead, sized as an order placed now would be - nothing to add.
        if (ahead(game, m, site, opening(game, 0))) return null;

        List<Weighed> weighed = new ArrayList<>();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            boolean candidate = m.kind() == Kind.ROADS
                    ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
            if (!candidate) continue;
            double unit = unit(game, m, t);
            // A building the city cannot staff at all, or one that does not
            // move the figure, serves nothing.
            if (!(unit > 0)) continue;
            // Counted twice: at today's demand with the slack, for the order's
            // own wait; then at the demand it opens to after that wait.
            int[] today = count(game, m, site, t, new Ahead(0, 1, SLACK));
            if (today[0] <= 0) continue;
            double lead = game.quoteBuild(t, today[0]).months;
            Ahead p = opening(game, lead);
            int[] found = count(game, m, site, t, p);
            if (found[0] <= 0) continue;
            boolean closes = found[1] == 1;
            boolean fits = t.getLandSqFt() * (double) found[0] <= landLeft;
            double per = (game.quoteBuild(t, 1).total + landValue(game, t.getLandSqFt(), landLeft)) / unit;
            weighed.add(new Weighed(t, (closes ? 0 : 2) + (fits ? 0 : 1), per, unit, found[0], closes, fits, p, lead));
        }
        if (weighed.isEmpty()) return null;
        /*
         * JUDGEMENTS, in this order: one that can keep the need ahead beats
         * one that cannot, at any price; of those, one whose whole count
         * fits the ground the cards before leave beats one the order would be
         * refused for (Game.buildStack()'s NO_LAND); then the lowest price a
         * unit with its ground. Land can be bought from the refusal's own
         * page; a building that cannot close a gap never will. A tie keeps
         * the catalogue's order (the sort is stable).
         */
        weighed.sort(java.util.Comparator.comparingInt(Weighed::tier).thenComparingDouble(Weighed::per));
        Weighed best = weighed.get(0);
        Weighed next = weighed.size() > 1 ? weighed.get(1) : null;
        Lost lost = next == null ? null
                : next.closes() != best.closes() ? Lost.CANNOT_CLOSE
                : next.fits() != best.fits() ? Lost.NO_ROOM : Lost.DEARER;

        int n = best.count();
        Game.BuildQuote q = game.quoteBuild(best.t(), n);
        boolean credit = q.total > cashLeft;
        double after = figure(game, m, plus(site, best.t(), n));
        double atOpening = figure(game, m, plus(site, best.t(), n), new Ahead(best.ahead().months(), best.ahead().k(), 0));
        double sq = best.t().getLandSqFt() * (double) n;
        return new Suggestion(need, m, best.t(), n, q.total, need.value(), whenOnSite, after, atOpening,
                best.closes(), credit, units(site), best.unit(), best.per(), best.ahead(), best.lead(),
                sq, landValue(game, sq, landLeft), Math.max(0, sq - Math.max(0, landLeft)),
                next == null ? null : next.t(), next == null ? Double.NaN : next.per(), lost,
                credit ? q.total - Math.max(0, cashLeft) : 0);
    }

    /**
     * The count before 0.7.51, kept for the harness's comparison: the least
     * n whose figure today is off the list (count() at no projection and no
     * slack, judged by clear() alone).
     */
    static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t) {
        return count(game, m, site, t, null);
    }

    /**
     * The count of t that keeps the need ahead at p's demand, after what is
     * on site (p null: the old rule, off the list today). {n, 1} when one
     * exists; when none does - the figure stops improving first - {n, 0}, n
     * the least count that reaches the best figure this building can make.
     */
    static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, Ahead p) {
        Ahead at = p == null ? NOW : p;
        java.util.function.Predicate<Map<BuildingsTemplate, Integer>> done = p == null
                ? a -> clear(m, figure(game, m, a)) : a -> ahead(game, m, a, at);
        double before = figure(game, m, site, at);
        boolean worseHigher = higherWorse(m);
        double best = before;
        int tried = 0, n = 1;
        while (n <= MOST) {
            Map<BuildingsTemplate, Integer> a = plus(site, t, n);
            double f = figure(game, m, a, at);
            if (done.test(a)) {
                int lo = tried, hi = n;           // lo fails (or is 0), hi is done
                while (hi - lo > 1) {
                    int mid = lo + (hi - lo) / 2;
                    if (done.test(plus(site, t, mid))) hi = mid; else lo = mid;
                }
                return new int[] {hi, 1};
            }
            boolean improved = worseHigher ? f < best - 1e-12 : f > best + 1e-12;
            if (!improved) break;
            best = f;
            tried = n;
            n *= 2;
        }
        if (tried == 0) return new int[] {0, 0};
        // The least count that reaches the best this building made.
        final double target = best;
        int lo = 0, hi = tried;
        while (hi - lo > 1) {
            int mid = lo + (hi - lo) / 2;
            double f = figure(game, m, plus(site, t, mid), at);
            boolean there = worseHigher ? f <= target + 1e-12 : f >= target - 1e-12;
            if (there) hi = mid; else lo = mid;
        }
        return new int[] {hi, 0};
    }

    /** Which way is worse for a measure's figure. */
    public static boolean higherWorse(Measure m) {
        switch (m.kind()) {
            case POWER: case WATER: case ROADS: case DEATH: case POLICE: case CELLS: return true;
            case SCHOOL: return !m.school().isBasic();
            default: return false;
        }
    }
}
