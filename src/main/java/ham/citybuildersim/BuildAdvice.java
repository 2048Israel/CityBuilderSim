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
 * THE RULE (the brief's section 4), in one paragraph: take NEEDS YOU's needs
 * in its order (CityNeeds.biting()) and keep those a city-built building
 * answers; for each, of the buildings that serve its measure (for the roads,
 * any road or line), the one with the lowest all-in price per unit of staffed
 * capacity - preferring one that can close the gap at all, and then one whose
 * whole count fits the land free; the count that closes the need's gap to
 * the line NEEDS YOU lists it at, after what is on site at its staffed
 * capacity, capped at what the cash left after the suggestions before it
 * affords; at most three, one per need. Every judgement in it is named
 * where it is made below, and in the project's design note for 0.7.24.
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
     * basic stage that is not the bottleneck, a school below a class's worth
     * of would-be students).
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
            case POWER: case WATER:
                return t.getProduction1() * staffing(t, fill);
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
       a basic stage's coverage, who would come over the seats, crime against
       Canada's, the caught not held.
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
        double[] fill = game.getPopulationManager().getJobFillRate();
        BuildingManager bm = game.getBuildingManager();
        switch (m.kind()) {
            case POWER:
            case WATER: {
                // UtilitiesHandler: production = (built + base) x the utilities'
                // pooled fill, Σ fill x jobs / Σ jobs over both utilities' posts.
                UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
                boolean power = m.kind() == Kind.POWER;
                int[] je = bm.getJobArrayPerCategory(BuildingType.ELECTRICITY);
                int[] jw = bm.getJobArrayPerCategory(BuildingType.WATER);
                double jobs = 0;
                for (int i = 0; i < je.length; i++) jobs += je[i] + jw[i];
                double filled = u.getAverageUtilityFill() * jobs;
                double base = power ? u.getBaseProduction() : u.getBaseWaterProduction();
                double demand = power ? u.getConsumption() : u.getWaterConsumption();
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    BuildingsTemplate t = e.getKey();
                    int n = e.getValue();
                    if (t.getCategory() == (power ? BuildingType.ELECTRICITY : BuildingType.WATER)) {
                        base += n * t.getProduction1();
                    }
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
                return supply > 0 ? demand / supply : Double.POSITIVE_INFINITY;
            }
            case ROADS:
                return roads(game, added).getUtilisation();
            case TRANSIT:
                return roads(game, added).getTransitCover();
            case CARE: {
                double cap = bm.getStaffedCareCapacity(m.care(), fill);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getCare() == m.care()) cap += e.getValue() * e.getKey().getCapacity() * staffing(e.getKey(), fill);
                }
                return Health.coverageOf(cap, m.care().populationServed(game.getCohorts()));
            }
            case DEATH:
                return unburiedNext(game, added);
            case PLOTS: {
                Healthcare h = game.getHealthcare();
                double plots = bm.getCareCapacity(CareType.BURIAL);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getCare() == CareType.BURIAL) plots += e.getValue() * (double) e.getKey().getCapacity();
                }
                return h.monthsOfPlotsLeft(plots);
            }
            case SCHOOL:
                return school(game, m.school(), added);
            case POLICE: {
                Crime crime = game.getCrime();
                double pop = crime.getPopulation();
                if (!(pop > 0)) return 0;
                double c = Crime.coverageOf(officers(game, added), pop);
                return crime.crimesAt(c) * 12 * 100_000.0 / pop / Crime.CANADA_CRIMES_PER_100K;
            }
            case CELLS: {
                double caught = game.getCrime().getCaught();
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
     * transit's riders against the commuters; the places, seats, officers
     * and cells against the people who need them (cells: six months of the
     * caught, a sentence each); the free plots and the ovens against the
     * dead waiting and dying. Burial plots, which are months and not a
     * stock against a need, read their plots left against two years of
     * burials.
     */
    public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        BuildingManager bm = game.getBuildingManager();
        switch (m.kind()) {
            case POWER: case WATER: {
                double load = figure(game, m, added);
                UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
                double demand = m.kind() == Kind.POWER ? u.getConsumption() : u.getWaterConsumption();
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    demand += e.getValue() * (m.kind() == Kind.POWER
                            ? e.getKey().getElectricityConsumption() : e.getKey().getWaterConsumption());
                }
                return new double[] {load > 0 ? demand / load : 0, demand};
            }
            case ROADS: {
                InfrastructureManager r = roads(game, added);
                return new double[] {r.getCapacity(), r.getEffectiveLoad()};
            }
            case TRANSIT: {
                InfrastructureManager r = roads(game, added);
                return new double[] {r.getUsableTransit(), r.getLoad(Traffic.COMMUTERS)};
            }
            case CARE: {
                double cap = bm.getStaffedCareCapacity(m.care(), fill);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getCare() == m.care()) cap += e.getValue() * e.getKey().getCapacity() * staffing(e.getKey(), fill);
                }
                return new double[] {cap, m.care().populationServed(game.getCohorts())};
            }
            case DEATH: {
                Healthcare h = game.getHealthcare();
                double toHandle = h.getDeaths() + h.getUnburied();
                double waiting = figure(game, m, added);
                return new double[] {toHandle - waiting, toHandle};
            }
            case PLOTS: {
                Healthcare h = game.getHealthcare();
                double plots = bm.getCareCapacity(CareType.BURIAL);
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getCare() == CareType.BURIAL) plots += e.getValue() * (double) e.getKey().getCapacity();
                }
                return new double[] {Healthcare.plotsRemaining(plots, h.getPlotsUsed()), h.getBurials() * CityNeeds.PLOTS_YELLOW};
            }
            case SCHOOL: {
                double[] places = bm.getStaffedEducationPlaces(fill);
                double seats = places[m.school().ordinal()];
                for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
                    if (e.getKey().getTeaches() == m.school()) seats += e.getValue() * e.getKey().getCapacity() * staffing(e.getKey(), fill);
                }
                double need;
                if (m.school().isBasic()) {
                    PopulationCohorts p = game.getCohorts();
                    double children = p.get(AgeBand.CHILD);
                    need = m.school() == EducationType.ELEMENTARY ? children * Education.ELEMENTARY_SHARE
                            : m.school() == EducationType.MIDDLE ? children * (1 - Education.ELEMENTARY_SHARE)
                            : p.get(AgeBand.TEEN);
                } else {
                    need = CityNeeds.wouldCome(game, m.school());
                }
                return new double[] {seats, need};
            }
            case POLICE:
                return new double[] {officers(game, added),
                        game.getCrime().getPopulation() * Crime.FULL_OFFICERS_PER_100K / 100_000.0};
            case CELLS:
                return new double[] {cells(game, added), game.getCrime().getCaught() * Crime.SENTENCE_MONTHS};
            default:
                return new double[] {0, 0};
        }
    }

    /** The figure as a share of what is needed, 0 to 1 - supply over demand, held to it - for a ring and the order bar's stacked bar. */
    public static double cover(Game game, Measure m, Map<BuildingsTemplate, Integer> added) {
        double[] sd = supplyDemand(game, m, added);
        if (!(sd[1] > 0)) return 1;
        return Math.max(0, Math.min(1, sd[0] / sd[1]));
    }

    /** The road network with these buildings standing: InfrastructureManager.with(), the model's own curve. */
    static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added) {
        double capacity = 0, highway = 0, transit = 0, load = 0;
        double[] streams = new double[Traffic.values().length];
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            int n = e.getValue();
            if (t.getCategory() == BuildingType.INFRASTRUCTURE) {
                capacity += n * (double) t.getCapacity();
                highway += n * t.getCapacity() * t.getFreightGrade();
                transit += n * t.getTransitCapacity();
            }
            load += n * t.getRoadLoad();
            for (Traffic s : Traffic.values()) streams[s.ordinal()] += n * t.loadOf(s);
        }
        return game.getInfrastructureManager().with(capacity, highway, transit, load, streams);
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
        Healthcare h = game.getHealthcare();
        BuildingManager bm = game.getBuildingManager();
        double[] fill = game.getPopulationManager().getJobFillRate();
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, fill);
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            BuildingsTemplate t = e.getKey();
            if (t.getCare() == CareType.BURIAL) plots += e.getValue() * (double) t.getCapacity();
            if (t.getCare() == CareType.CREMATION) ovens += e.getValue() * t.getCapacity() * staffing(t, fill);
        }
        double free = Healthcare.plotsRemaining(plots, h.getPlotsUsed());
        double waiting = Math.max(0, h.getDeaths() + h.getUnburied() - free - Math.max(0, ovens));
        return Math.min(waiting, h.getDeaths() * Healthcare.MAX_BACKLOG_MONTHS);
    }

    /** A school's figure: a basic stage's coverage (Education's own cover, places over the children it serves), or who would come over the seats above the ladder. */
    static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double[] places = game.getBuildingManager().getStaffedEducationPlaces(fill);
        double seats = places[type.ordinal()];
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            if (e.getKey().getTeaches() == type) seats += e.getValue() * e.getKey().getCapacity() * staffing(e.getKey(), fill);
        }
        if (type.isBasic()) {
            PopulationCohorts p = game.getCohorts();
            double children = p.get(AgeBand.CHILD), teens = p.get(AgeBand.TEEN);
            double need = type == EducationType.ELEMENTARY ? children * Education.ELEMENTARY_SHARE
                    : type == EducationType.MIDDLE ? children * (1 - Education.ELEMENTARY_SHARE) : teens;
            if (need <= 0) return seats > 0 ? 1 : 0;
            return Math.max(0, Math.min(1, seats / need));
        }
        return CityNeeds.wouldCome(game, type) / Math.max(seats, 1);
    }

    static double officers(Game game, Map<BuildingsTemplate, Integer> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double n = game.getBuildingManager().getStaffedSafetyCapacity(SafetyType.POLICE, fill);
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            if (e.getKey().getSafety() == SafetyType.POLICE) n += e.getValue() * e.getKey().getCapacity() * staffing(e.getKey(), fill);
        }
        return n;
    }

    static double cells(Game game, Map<BuildingsTemplate, Integer> added) {
        double[] fill = game.getPopulationManager().getJobFillRate();
        double n = game.getBuildingManager().getStaffedSafetyCapacity(SafetyType.PRISON, fill);
        for (Map.Entry<BuildingsTemplate, Integer> e : added.entrySet()) {
            if (e.getKey().getSafety() == SafetyType.PRISON) n += e.getValue() * e.getKey().getCapacity() * staffing(e.getKey(), fill);
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
     * Build charges it, added in their order - WHAT WOULD HELP MOST's "all
     * three ≈ $X" and the look of its "Build all three", which the screen
     * added up itself until now. Two of one building stay two orders, as
     * the screen places them one after another. Reads; changes nothing.
     */
    public static double quoteTotal(List<Suggestion> advice) {
        double total = 0;
        for (Suggestion s : advice) total += s.price();
        return total;
    }

    /* =====================================================================
       THE SUGGESTIONS
       ===================================================================== */

    /** At most this many suggestions, one per need. */
    public static final int MAX_SUGGESTIONS = 3;

    /** The most of one building a search will count to. */
    static final int MOST = 1 << 22;

    /**
     * One suggested order.
     *
     * @param count      what to order: the full count, or what the cash affords
     * @param fullCount  what closes the need's gap after what is on site
     * @param price      the quote for count - Game.quoteBuild(), what Build charges
     * @param fullPrice  the quote for fullCount
     * @param before     the need's figure now (NEEDS YOU's)
     * @param whenOnSite the figure once what is on site opens
     * @param after      ...and with this order too
     * @param closes     false when no count of this building takes the need
     *                   off the list (crime with every officer it can use,
     *                   transit past the share of commuters who will ride):
     *                   then count is where it stops helping
     * @param capped     the count is what the cash left affords, less than full
     * @param needsCredit the cash left affords none: the full count, on credit
     * @param landShort  square feet the order is short of the land free, or 0
     * @param onSite     units on site that already serve the measure
     */
    public record Suggestion(CityNeeds.Need need, Measure measure, BuildingsTemplate template,
                             int count, int fullCount, double price, double fullPrice,
                             double before, double whenOnSite, double after,
                             boolean closes, boolean capped, boolean needsCredit,
                             double landShort, int onSite, double unit, double pricePerUnit) { }

    /** The rule, on this city now. */
    public static List<Suggestion> suggest(Game game) {
        return suggest(game, CityNeeds.measure(game, CityNeeds.PLAIN));
    }

    /** The rule, on NEEDS YOU as already measured (the screen measures it once a redraw). */
    public static List<Suggestion> suggest(Game game, List<CityNeeds.Need> all) {
        List<Suggestion> out = new ArrayList<>();
        double cashLeft = game.getCash();
        for (CityNeeds.Need need : CityNeeds.biting(all)) {
            if (out.size() >= MAX_SUGGESTIONS) break;
            if (!need.cityBuilds()) continue;
            Measure m = measureOf(need);
            if (m == null) continue;
            Suggestion s = suggestFor(game, need, m, cashLeft);
            if (s == null) continue;
            out.add(s);
            cashLeft = Math.max(0, cashLeft - s.price());
        }
        return out;
    }

    /** One need's suggestion, or null when what is on site already takes it off the list or no building can help. */
    static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft) {
        Map<BuildingsTemplate, Integer> site = onSite(game, m);
        double whenOnSite = figure(game, m, site);
        // JUDGEMENT: what is on site already closes it - nothing to add.
        if (clear(m, whenOnSite)) return null;

        BuildingsTemplate best = null;
        double bestPer = Double.MAX_VALUE, bestUnit = 0;
        int bestCount = 0, bestTier = Integer.MAX_VALUE;
        boolean bestCloses = false;
        double landFree = game.getLandManager().getAvailableSqFt();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            boolean candidate = m.kind() == Kind.ROADS
                    ? t.getCategory() == BuildingType.INFRASTRUCTURE : m.serves(t);
            if (!candidate) continue;
            double unit = unit(game, m, t);
            // A building the city cannot staff at all, or one that does not
            // move the figure, serves nothing.
            if (!(unit > 0)) continue;
            double per = game.quoteBuild(t, 1).total / unit;
            int[] found = count(game, m, site, t);
            if (found[0] <= 0) continue;
            boolean closes = found[1] == 1;
            boolean fits = t.getLandSqFt() * (double) found[0] <= landFree;
            /*
             * JUDGEMENTS, in this order: one that can close the gap beats one
             * that cannot, at any price; of those, one whose whole count fits
             * the ground the city has free beats one the order would be
             * refused for (Game.buildStack()'s NO_LAND); then the lowest price
             * per unit. Land can be bought from the refusal's own page; a
             * building that cannot close a gap never will.
             */
            int tier = (closes ? 0 : 2) + (fits ? 0 : 1);
            boolean better = best == null || tier < bestTier || (tier == bestTier && per < bestPer);
            if (better) {
                best = t; bestPer = per; bestUnit = unit; bestCount = found[0]; bestCloses = closes; bestTier = tier;
            }
        }
        if (best == null) return null;

        int full = bestCount;
        double fullPrice = game.quoteBuild(best, full).total;
        int n = full;
        boolean capped = false, credit = false;
        if (fullPrice > cashLeft) {
            int affords = affordable(game, best, full, cashLeft);
            if (affords >= 1) { n = affords; capped = true; }
            else credit = true;
        }
        Game.BuildQuote q = game.quoteBuild(best, n);
        double after = figure(game, m, plus(site, best, n));
        return new Suggestion(need, m, best, n, full, q.total, fullPrice,
                need.value(), whenOnSite, after, bestCloses, capped, credit,
                Math.max(0, q.landNeeded - q.landFree), units(site), bestUnit, bestPer);
    }

    /**
     * The count of t that closes the need, after what is on site: the least
     * n whose figure is off the list. {n, 1} when one exists; when none does
     * - the figure stops improving first - {n, 0}, n the least count that
     * reaches the best figure this building can make.
     */
    static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t) {
        double before = figure(game, m, site);
        boolean worseHigher = higherWorse(m);
        double best = before;
        int tried = 0, n = 1;
        while (n <= MOST) {
            double f = figure(game, m, plus(site, t, n));
            if (clear(m, f)) {
                int lo = tried, hi = n;           // lo fails (or is 0), hi clears
                while (hi - lo > 1) {
                    int mid = lo + (hi - lo) / 2;
                    if (clear(m, figure(game, m, plus(site, t, mid)))) hi = mid; else lo = mid;
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
            double f = figure(game, m, plus(site, t, mid));
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

    /** The most of t up to `full` whose quote the cash covers; 0 for none. */
    static int affordable(Game game, BuildingsTemplate t, int full, double cash) {
        if (!(cash > 0) || game.quoteBuild(t, 1).total > cash) return 0;
        int lo = 1, hi = full;                    // lo affordable, hi not
        while (hi - lo > 1) {
            int mid = lo + (hi - lo) / 2;
            if (game.quoteBuild(t, mid).total <= cash) lo = mid; else hi = mid;
        }
        return lo;
    }
}
