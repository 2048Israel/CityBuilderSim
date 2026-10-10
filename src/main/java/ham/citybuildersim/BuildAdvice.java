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
 * buildings that serve its measure (for the roads, any road or line, and
 * the city's gravel roads paved beside them), the one whose order costs
 * least over its life for each unit it will serve (since 0.7.101,
 * lifePerServed(): its quote, its ground at what the city would pay to
 * replace it - landValue(), the land office's price - and its running for
 * BUILD_BOND_YEARS, as a road's has been since 0.7.70; over what it will
 * serve of the need as the city grows into it, for power, water, the
 * roads and care, so a building far bigger than the need pays for what
 * stands idle, and what its count adds for the rest; see A BUILDING OVER
 * ITS LIFE) - preferring one that can keep the need ahead at all, and
 * then one
 * whose whole count fits the ground the suggestions before it leave; the
 * count that keeps the need ahead at the demand it opens to, projected
 * the way the businesses project theirs (above the ladder, the students it
 * would get and hire by then, its feeder's graduates counted) and SLACK
 * past it - off NEEDS YOU's list there, and a served gauge at 100% of it -
 * after what is on site at its staffed capacity; on credit when its quote
 * is more than the cash the ones before leave, never cut to the cash; at
 * most three, one per need. Every judgement in it is named where it is made
 * below, and in the project's design notes for 0.7.24 and 0.7.51. A first
 * building where none stands is suggested however far past the need one
 * goes (Jerus's school at 0% that one would take to 20,000%): the overshoot
 * ranks the candidates, it never stops one (0.7.101).
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
        // ...and the city's sea terminals (0.7.86, batch O9), their own group beside oil storage (BuildCard, star O9-1).
        out.add(new Category(INDUSTRY,    "Industrial", EnumSet.of(BuildingType.INDUSTRIAL,
                BuildingType.HEAVY_INDUSTRY, BuildingType.MINING, BuildingType.CONSTRUCTION, BuildingType.PORTS), false));
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
        // ...and the gravel roads being paved (0.7.70; ConstructionControl,
        // F): each goes as its Paved Road on site opens, so the road once
        // what is on site opens is without them.
        int paving = game.pavingNow();
        BuildingsTemplate gravel = paving > 0 && m.kind() == Kind.ROADS
                ? game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM) : null;
        if (gravel != null) out.merge(gravel, -paving, Integer::sum);
        return out;
    }

    /** Units on site, all told: the buildings going up (a gravel road a paving takes away is not one). */
    public static int units(Map<BuildingsTemplate, Integer> added) {
        int n = 0;
        for (int v : added.values()) if (v > 0) n += v;
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
        return verdict(game, s.measure(), plus(onSite(game, s.measure()), added(game, s)));
    }

    /** What a suggestion changes on the city: its count of its building - and for a paving (0.7.70), as many gravel roads gone. */
    public static Map<BuildingsTemplate, Integer> added(Game game, Suggestion s) {
        return s.paving() ? times(new LinkedHashMap<>(), pavingStep(game), s.count())
                : plus(new LinkedHashMap<>(), s.template(), s.count());
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
        return opening(game, lead, SLACK);
    }

    /** ...with another slack past it (0.7.73): automatic building's, the player's slider (AutoBuilder). */
    public static Ahead opening(Game game, double lead, double slack) {
        double wait = Double.isNaN(lead) ? 0 : Math.max(0, Math.min(lead, BusinessInvestment.MAX_ORDER_MONTHS));
        double months = wait + HORIZON;
        return new Ahead(months, game.getBusinessInvestment().growthFactor(months), slack);
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

    /** Why the next building in the ranking lost to the one suggested: dearer over its life for what it serves (a unit with its land until 0.7.101), it cannot keep the need ahead, or its count needs more land than is left. */
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
     * @param pricePerUnit the figure it was ranked by (lifePerServed(), since
     *                    0.7.101): its order over its life, its ground at
     *                    landValue() on the land the ones before leave, for
     *                    each unit it will serve over that life
     * @param ahead       the demand it is sized to (opening(lead))
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
                             int count, double price, double before, double whenOnSite, double after,
                             double afterAtOpening, boolean closes, boolean needsCredit, int onSite,
                             double unit, double pricePerUnit, Ahead ahead, double lead,
                             double landSqFt, double landValue, double landShort,
                             BuildingsTemplate runnerUp, double runnerUpPer, Lost runnerUpLost, double credit,
                             boolean paving) { }

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

    /** The suggestions as one run, as "Build all three" places it: each card's building and count in their order, two of one building added together - and no paving (0.7.70), which is not a build order and has its card's own button. */
    public static LinkedHashMap<BuildingsTemplate, Integer> run(List<Suggestion> advice) {
        LinkedHashMap<BuildingsTemplate, Integer> run = new LinkedHashMap<>();
        for (Suggestion s : advice) if (!s.paving()) run.merge(s.template(), s.count(), Integer::sum);
        return run;
    }

    /** The suggestions "Build all three" places (0.7.70): every one but a paving. */
    public static List<Suggestion> builds(List<Suggestion> advice) {
        List<Suggestion> out = new ArrayList<>();
        for (Suggestion s : advice) if (!s.paving()) out.add(s);
        return out;
    }

    /** One building weighed for a need: its count at its projection, and where it ranks - or, paving, gravel roads paved (0.7.70). */
    private record Weighed(BuildingsTemplate t, int tier, double per, double unit, int count,
                           boolean closes, boolean fits, Ahead ahead, double lead, boolean paving) { }

    /** One need's suggestion, or null when what is on site already keeps it ahead or no building can help. */
    static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft) {
        return suggestFor(game, need, m, cashLeft, landLeft, SLACK);
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

    /** ...with the buildings in `skip` left out of the ranking (0.7.73): automatic building's, for one the money or the ground cannot pay for one of (since 0.7.101; its budget could not run one until then) - the next in the ranking is the card. */
    public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft,
                                        double slack, java.util.Set<BuildingsTemplate> skip) {
        return suggestFor(game, need, m, cashLeft, landLeft, slack, skip, false);
    }

    /** ...and with the city's gravel roads paved left out too when `noPaving` (0.7.101): a paving the money cannot pay for, passed over the same way. */
    public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft,
                                        double slack, java.util.Set<BuildingsTemplate> skip, boolean noPaving) {
        Map<BuildingsTemplate, Integer> site = onSite(game, m);
        double whenOnSite = figure(game, m, site);
        // JUDGEMENT: what is on site already keeps it ahead, sized as an order placed now would be - nothing to add.
        if (ahead(game, m, site, opening(game, 0, slack))) return null;
        double before = need != null ? need.value() : figure(game, m, new LinkedHashMap<>());

        List<Weighed> weighed = new ArrayList<>();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            boolean candidate = m.kind() == Kind.ROADS
                    ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
            if (!candidate || skip.contains(t)) continue;
            double unit = unit(game, m, t);
            // A building the city cannot staff at all, or one that does not
            // move the figure, serves nothing.
            if (!(unit > 0)) continue;
            // Counted twice: at today's demand with the slack, for the order's
            // own wait; then at the demand it opens to after that wait.
            int[] today = count(game, m, site, t, new Ahead(0, 1, slack));
            if (today[0] <= 0) continue;
            double lead = game.quoteBuild(t, today[0]).months;
            Ahead p = opening(game, lead, slack);
            int[] found = count(game, m, site, t, p);
            if (found[0] <= 0) continue;
            boolean closes = found[1] == 1;
            boolean fits = t.getLandSqFt() * (double) found[0] <= landLeft;
            // Every candidate's figure since 0.7.101: its order over its life
            // for each unit it will serve - see A BUILDING OVER ITS LIFE. (A
            // road's was its cost over its life a trip from 0.7.70, a living
            // care building's its order over the places the need lacks from
            // 0.7.71, and the rest their price a unit with their ground.)
            double per = lifePerServed(game, m, site, t, found[0], unit, p, landLeft);
            weighed.add(new Weighed(t, (closes ? 0 : 2) + (fits ? 0 : 1), per, unit, found[0], closes, fits, p, lead,
                    false));
        }
        // ...and the city's gravel roads, paved (0.7.70): no ground to fit.
        Weighed paving = m.kind() == Kind.ROADS && !noPaving ? weighPaving(game, m, site, landLeft, slack) : null;
        if (paving != null) weighed.add(paving);
        if (weighed.isEmpty()) return null;
        /*
         * JUDGEMENTS, in this order: one that can keep the need ahead beats
         * one that cannot, at any price; of those, one whose whole count
         * fits the ground the cards before leave beats one the order would be
         * refused for (Game.buildStack()'s NO_LAND); then the least over its
         * life for each unit it will serve (0.7.101). Land can be bought from the refusal's own
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
        if (best.paving()) {
            // A paving (0.7.70): its quote, its gravel roads gone, and the
            // ground it frees taken off the land the ones after it see.
            Game.BuildQuote q = game.quotePave(n);
            boolean credit = q.total > cashLeft;
            Map<BuildingsTemplate, Integer> done = times(site, pavingStep(game), n);
            double freed = pavingFrees(game) * (double) n;
            return new Suggestion(need, m, best.t(), n, q.total, before, whenOnSite, figure(game, m, done),
                    figure(game, m, done, new Ahead(best.ahead().months(), best.ahead().k(), 0)),
                    best.closes(), credit, units(site), best.unit(), best.per(), best.ahead(), best.lead(),
                    -freed, -landValue(game, freed, landLeft), 0,
                    next == null ? null : next.t(), next == null ? Double.NaN : next.per(), lost,
                    credit ? q.total - Math.max(0, cashLeft) : 0, true);
        }
        Game.BuildQuote q = game.quoteBuild(best.t(), n);
        boolean credit = q.total > cashLeft;
        double after = figure(game, m, plus(site, best.t(), n));
        double atOpening = figure(game, m, plus(site, best.t(), n), new Ahead(best.ahead().months(), best.ahead().k(), 0));
        double sq = best.t().getLandSqFt() * (double) n;
        return new Suggestion(need, m, best.t(), n, q.total, before, whenOnSite, after, atOpening,
                best.closes(), credit, units(site), best.unit(), best.per(), best.ahead(), best.lead(),
                sq, landValue(game, sq, landLeft), Math.max(0, sq - Math.max(0, landLeft)),
                next == null ? null : next.t(), next == null ? Double.NaN : next.per(), lost,
                credit ? q.total - Math.max(0, cashLeft) : 0, false);
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
        return count(game, m, site, plus(new LinkedHashMap<>(), t, 1), p, MOST);
    }

    /**
     * ...of a step that may be more than one building (0.7.70): a paving
     * is a Paved Road more and a Gravel Road less (pavingStep()); and at most
     * `most` of it, the gravel roads there are to pave. The search the count
     * always made, n doubling to MOST: to the bit the same for one building.
     */
    static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Integer> step,
                       Ahead p, int most) {
        if (most < 1) return new int[] {0, 0};
        Ahead at = p == null ? NOW : p;
        java.util.function.Predicate<Map<BuildingsTemplate, Integer>> done = p == null
                ? a -> clear(m, figure(game, m, a)) : a -> ahead(game, m, a, at);
        double before = figure(game, m, site, at);
        boolean worseHigher = higherWorse(m);
        double best = before;
        int tried = 0, n = 1;
        while (true) {
            Map<BuildingsTemplate, Integer> a = times(site, step, n);
            double f = figure(game, m, a, at);
            if (done.test(a)) {
                int lo = tried, hi = n;           // lo fails (or is 0), hi is done
                while (hi - lo > 1) {
                    int mid = lo + (hi - lo) / 2;
                    if (done.test(times(site, step, mid))) hi = mid; else lo = mid;
                }
                return new int[] {hi, 1};
            }
            boolean improved = worseHigher ? f < best - 1e-12 : f > best + 1e-12;
            if (!improved) break;
            best = f;
            tried = n;
            if (n >= most) break;
            n = (int) Math.min(2L * n, most);
        }
        if (tried == 0) return new int[] {0, 0};
        // The least count that reaches the best this building made.
        final double target = best;
        int lo = 0, hi = tried;
        while (hi - lo > 1) {
            int mid = lo + (hi - lo) / 2;
            double f = figure(game, m, times(site, step, mid), at);
            boolean there = worseHigher ? f <= target + 1e-12 : f >= target - 1e-12;
            if (there) hi = mid; else lo = mid;
        }
        return new int[] {hi, 0};
    }

    /** A copy of base with n times every quantity of step added. */
    static Map<BuildingsTemplate, Integer> times(Map<BuildingsTemplate, Integer> base, Map<BuildingsTemplate, Integer> step, int n) {
        Map<BuildingsTemplate, Integer> out = new LinkedHashMap<>(base);
        if (n != 0) for (Map.Entry<BuildingsTemplate, Integer> e : step.entrySet()) out.merge(e.getKey(), n * e.getValue(), Integer::sum);
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
       (perPlaceNeeded(), gone): the order's own quote - Game.quoteBuild() for
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
       a candidate beside the six (weighPaving()), priced the same way for
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
       ===================================================================== */

    /** Months a road is weighed over (0.7.70): the funding page's bond term, Game.BUILD_BOND_YEARS - a long-lived asset "paid for over the years the city uses it". */
    public static final int LIFE_MONTHS = Game.BUILD_BOND_YEARS * 12;

    /**
     * What one costs the city a month, standing (0.7.70): its repairs - one
     * percent a year of what one costs to put up today, its work and its
     * material, with the builders' tax: the rule the month charges every
     * city building by (EconomyManager.maintenanceBillFor(),
     * RealEstate.MAINTENANCE_PER_YEAR) - its power and water at the
     * utility's prices (BusinessInvestment.runningCostOf()'s reading), its
     * posts at today's wages and its upkeep (BuildCard.runningCost()), less a
     * month of fares from the riders it adds on the network with what is on
     * site (TaxPolicy.monthlyFare(), LongPlaytest.linesThatPay()'s reading):
     * a line's, and a road's only where the lines wait for road under them.
     */
    public static double running(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Integer> site) {
        BuildingManager bm = game.getBuildingManager();
        EconomyManager econ = game.getEconomyManager();
        double materials = Math.max(0, bm.getConstructionMaterialPrice());
        double repairs = econ.withBuildersTax(bm.nonMaterialCost(t) + t.getConstructionMaterials() * materials)
                * ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12;
        double utilities = t.getElectricityConsumption() * econ.getPricePerWatt()
                + t.getWaterConsumption() * econ.getPricePerWaterUnit();
        double riders = roads(game, plus(site, t, 1)).getTransitRiders() - roads(game, site).getTransitRiders();
        double fares = riders * econ.getTaxPolicy().monthlyFare();
        return repairs + utilities + BuildCard.runningCost(game, t) - fares;
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
        return game.getBusinessInvestment().realTestRate(game.getDebtManager().quoteRate(0, LIFE_MONTHS));
    }

    /** A road's or a line's cost over its life (0.7.70): its quote for one, its ground at landValue() on the land left, and lifeFactor() months of running(). */
    public static double lifetime(Game game, BuildingsTemplate t, double landLeft, Map<BuildingsTemplate, Integer> site) {
        return game.quoteBuild(t, 1).total + landValue(game, t.getLandSqFt(), landLeft)
                + running(game, t, site) * lifeFactor(game);
    }

    /** One paving (0.7.70): a Paved Road more and a Gravel Road less; empty with either missing from the catalogue. */
    static Map<BuildingsTemplate, Integer> pavingStep(Game game) {
        Map<BuildingsTemplate, Integer> step = new LinkedHashMap<>();
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        if (from == null || to == null) return step;
        step.put(to, 1);
        step.put(from, -1);
        return step;
    }

    /** The ground one paving frees (0.7.70): a gravel road's less a paved road's; 0 with either missing. */
    public static double pavingFrees(Game game) {
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        return from == null || to == null ? 0 : Math.max(0, from.getLandSqFt() - to.getLandSqFt());
    }

    /** The trips one paving takes off the road (0.7.70): over the line with what is on site, less with one more gravel road paved. */
    public static double pavingUnit(Game game, Map<BuildingsTemplate, Integer> site) {
        Map<BuildingsTemplate, Integer> step = pavingStep(game);
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
    public static double pavingLifetime(Game game, double landLeft, Map<BuildingsTemplate, Integer> site) {
        Game.BuildQuote q = game.quotePave(1);
        if (q == null) return Double.NaN;
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        return q.total - landValue(game, pavingFrees(game), landLeft)
                + (running(game, to, site) - running(game, from, site)) * lifeFactor(game);
    }

    /**
     * Whether paving beats a new Paved Road (0.7.70), each over its life a
     * trip it takes off: pavingLifetime() over pavingUnit() under a Paved
     * Road's lifetime() over unit() - Jerus's "recommends this if better".
     * False with nothing to pave.
     */
    public static boolean pavingBeatsPaved(Game game, double landLeft, Map<BuildingsTemplate, Integer> site) {
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
    static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Integer> site, double landLeft) {
        return weighPaving(game, m, site, landLeft, SLACK);
    }

    /** ...at another slack past its projection (0.7.73, AutoBuilder). */
    static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Integer> site, double landLeft, double slack) {
        int most = game.paveable();
        Map<BuildingsTemplate, Integer> step = pavingStep(game);
        if (most < 1 || step.isEmpty()) return null;
        double unit = pavingUnit(game, site);
        if (!(unit > 0) || !pavingBeatsPaved(game, landLeft, site)) return null;
        int[] today = count(game, m, site, step, new Ahead(0, 1, slack), most);
        if (today[0] <= 0) return null;
        double lead = game.quotePave(today[0]).months;
        Ahead p = opening(game, lead, slack);
        int[] found = count(game, m, site, step, p, most);
        if (found[0] <= 0) return null;
        boolean closes = found[1] == 1;
        // Its figure since 0.7.101 as every candidate's (A BUILDING OVER ITS LIFE): the pavings' order over its
        // life for each trip it will take off; pavingLifetime() over pavingUnit() until then.
        double served = servedOverLife(game, m, site, times(new LinkedHashMap<>(), step, found[0]), p);
        double life = pavingOrderLife(game, found[0], landLeft, site);
        double per = perServed(life, served, found[0], unit);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        return new Weighed(to, closes ? 0 : 2, per, unit, found[0], closes, true, p, lead, true);
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
           line) and care (the places past what stands): in each year of its
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
                                   Map<BuildingsTemplate, Integer> site) {
        return game.quoteBuild(t, count).total + landValue(game, t.getLandSqFt() * (double) count, landLeft)
                + count * running(game, t, site) * lifeFactor(game);
    }

    /** ...`count` pavings' (0.7.101): their quote (Game.quotePave()), less the ground they free at landValue() on the land left, and lifeFactor() months of a paved road's running over a gravel road's for each; NaN with nothing to pave. */
    public static double pavingOrderLife(Game game, int count, double landLeft, Map<BuildingsTemplate, Integer> site) {
        Game.BuildQuote q = game.quotePave(count);
        BuildingsTemplate from = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        if (q == null || from == null || to == null) return Double.NaN;
        return q.total - landValue(game, pavingFrees(game) * (double) count, landLeft)
                + count * (running(game, to, site) - running(game, from, site)) * lifeFactor(game);
    }

    /**
     * What a measure is short of, in its own unit, with these buildings
     * standing as well, at `p`'s demand (0.7.101): power and water the supply
     * NEEDS YOU's quarter in hand asks (the demand over NETWORK_YELLOW) less
     * the supply; the roads the trips over STRAINED of their capacity
     * (roadsOver()'s line, the unit a road's unit() is in); care the places
     * past what stands. NaN for a measure not weighed so.
     */
    public static double shortfall(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p) {
        switch (m.kind()) {
            case POWER: case WATER: {
                double[] sd = supplyDemand(game, m, added, p);
                return sd[1] / CityNeeds.NETWORK_YELLOW - sd[0];
            }
            case ROADS: {
                InfrastructureManager r = roads(game, added, p.scale());
                return r.getEffectiveLoad() - InfrastructureManager.STRAINED * r.getCapacity();
            }
            case CARE: {
                double[] sd = supplyDemand(game, m, added, p);
                return sd[1] - sd[0];
            }
            default:
                return Double.NaN;
        }
    }

    /** Whether a measure's candidates are weighed for what they will serve over their lives (shortfall()): power, water, the roads and care. */
    public static boolean weighsServed(Measure m) {
        switch (m.kind()) {
            case POWER: case WATER: case ROADS: case CARE: return true;
            default: return false;
        }
    }

    /**
     * What an order serves over its life, a month on average, in the
     * measure's unit (0.7.101): each of its LIFE_YEARS years read at its
     * middle month - from its opening, `p`'s months less HORIZON, the demand
     * today's times lifeTrend() with p's slack past it - as the shortfall
     * with what stands and is on site (`site`) less the shortfall with the
     * order too (both held at nothing), each year weighted by its twelve
     * months discounted as lifeFactor() discounts them. NaN for a measure not
     * weighed so (weighsServed()).
     */
    public static double servedOverLife(Game game, Measure m, Map<BuildingsTemplate, Integer> site,
                                        Map<BuildingsTemplate, Integer> order, Ahead p) {
        if (!weighsServed(m)) return Double.NaN;
        Map<BuildingsTemplate, Integer> with = plus(site, order);
        double opens = Math.max(0, p.months() - HORIZON);
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
     * A candidate's figure, the one the advice ranks by (0.7.101): an order
     * of `count` of t over its life (orderLife()) for each unit it will serve
     * over that life (servedOverLife() at `p`, the demand it is sized to;
     * what its count adds where the measure is not weighed so).
     */
    public static double lifePerServed(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t,
                                       int count, double unit, Ahead p, double landLeft) {
        double served = servedOverLife(game, m, site, plus(new LinkedHashMap<>(), t, count), p);
        return perServed(orderLife(game, t, count, landLeft, site), served, count, unit);
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

    /** Whether an offer holding iron would pay itself back in this city (fieldEarnings() against fieldPayment()). */
    public static Payback ironPayback(Game game, LandParcel field) {
        double[] earned = fieldEarnings(game, field.getDeposits());
        return new Payback((int) earned[0], earned[1], fieldPayment(game, field));
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
        BuildingsTemplate mine = null;
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals("Iron Mine")) { mine = t; break; }
        }
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
            case POWER: case WATER: case ROADS: case DEATH: case POLICE: case CELLS: return true;
            case SCHOOL: return !m.school().isBasic();
            default: return false;
        }
    }
}
