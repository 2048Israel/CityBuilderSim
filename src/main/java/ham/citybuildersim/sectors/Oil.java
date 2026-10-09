package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.BuildingsStacks;
import ham.citybuildersim.Deposit;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Sectors;
import ham.citybuildersim.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Oil wells. THE SIXTEENTH SECTOR (0.7.62, batch K; the project's
 * spec-land.md 2.7).
 *
 * MINING'S SHAPE, ON THE OTHER RESOURCE. The world laid oil in fields from
 * the start (Resource.OIL, batch J1a) and the land office has sold the
 * ground over it since J1b; this is what lifts it. A well stands on an
 * unworked oil site the city owns (Game.hasDepositFor(), by its good), lifts
 * 415 t of crude a month - a hundred barrels a day - and ships what it
 * lifts: CRUDE is a flow good, so what the refiners do not take at the
 * local price leaves at the export price the same month, which is why a
 * well is worth drilling before there is a refinery. The ground limits it
 * through the template's hook (groundLimit()), and the wells retire when
 * the oil runs out, as the mines do when the ore does.
 *
 * SINCE 0.7.84 (batch O7; runs/spec-oil.md 2.6) A WELL DECLINES: it lifts
 * its 415 t the month it opens and 10% less each year after, and is retired
 * below ten barrels a day, after 263 months (THE WELLS DECLINE, below); a
 * land well stands only on a dry site - a field whose centre is on land -
 * and has one post, not three.
 *
 * SINCE 0.7.91 (batch O10; runs/spec-oil.md 2.7, 2.11) THE SEA'S OIL IS
 * LIFTED TOO: an Offshore Platform stands on a shallow sea field the city
 * owns, its Platform Wells lift in its slots, shuttle tankers take their
 * crude ashore at the boundary's freight, and a Crude Pipeline replaces
 * them where the freight it saves over the field's life repays it (THE OIL
 * AT SEA, below). The planner weighs the three kinds after the land well.
 *
 * Everything else - the books, the credit, the market, the payroll, the
 * export - is the template's.
 */
public final class Oil extends Sector {

    public Oil() {
        super("Oil", "Oil", BuildingType.MINING);
        makes(Good.CRUDE);
        blurb("Lifts crude from the city's oil fields and sells it to the refinery, or ships "
                + "it abroad when the refinery does not want it. The oil the city owns is the "
                + "limit, and the city has to buy the ground it is under.");
    }

    /** The ground, not the well, decides what comes up. */
    @Override
    protected double groundLimit(Good g, double asked) {
        if (game == null || g != Good.CRUDE) return asked;
        LandManager land = game.getLandManager();
        return land == null ? asked : land.extractOil(asked);
    }

    /** What the wells could lift this month if the ground allowed it. */
    public double getPotentialOutput() {
        return getCapacity(Good.CRUDE) * getOperatingRate();
    }

    /* =====================================================================
       THE WELLS DECLINE (0.7.84, batch O7; runs/spec-oil.md 2.6, the
       project's oil-and-ports-research.md 3)

       A well lifted its 415 t a month - a hundred barrels a day - for as
       long as its field had oil. A real one does not: an onshore well loses
       10-25% in its first year and a field 4.2% a year [W6]; a well on a
       shallow-water platform holds a plateau of two or three years and then
       loses 8.5% a year [W6][W16]. The research's proposal (3.2): each land
       well loses 10% a year (est.) and retires below ten barrels a day, and
       the sector's one-well-a-month rule refills its site.

       VINTAGES. The wells are kept as vintages - the month a batch of them
       opened, how many, and their kind (LAND, and since 0.7.91 PLATFORM) -
       saved in the sector's extras as vintages.<month>.<KIND>. A well lifts
       its template's nameplate times its kind's profile at its age in
       months (profile()), so the sector's nameplate is the sum over its
       vintages (getCapacity()), struck once a month and cached on it.

       ONE COUNT, MANY WAYS TO MOVE IT. Wells open (the month's completions,
       a fixture's stand, a load) and close (worn out, the shrinking rules, a
       demolition, a buy-out) by paths that know nothing of vintages. So the
       vintages are read against the wells standing (view()): wells they do
       not hold are new this month, and wells they hold that no longer stand
       were the oldest - the retirements below take the oldest first, and
       every other rule that sells a well is taken to sell its least
       productive one. The vintages are struck to that reading once a month,
       at its end (endOfMonth()), after the month's completions have lifted:
       a well lifts its whole nameplate the month it opens, and the month's
       retirements, at its top (Game.runRetirement()), read its age then.
       A read never moves them, so nothing a screen reads changes the month.

       A save from before 0.7.84 has no vintages: every well it stands is
       read as new, and struck as opened the first month the city plays.
       ===================================================================== */

    /** A well's kind: on land, on a dry site; or in an offshore platform's slot (0.7.91, THE OIL AT SEA). Saved by name. */
    public enum WellKind { LAND, PLATFORM }

    /** A batch of wells: the month they opened, how many, and their kind. */
    public record Vintage(int month, int count, WellKind kind) { }

    /** A land well keeps nine tenths of its lift a year: it loses 10% a year (the research's 3.2, est.; [W6]). */
    public static final double LAND_KEEPS_A_YEAR = .9;

    /** A platform well holds its lift for a plateau of three years (the research's 3.1: two or three [W6][W16]; spec-oil 2.6)... */
    public static final int PLATFORM_PLATEAU_MONTHS = 36;

    /** ...and then keeps 91.5% of it a year: it loses 8.5% a year, the IEA's shallow-offshore rate [W6][W16]. */
    public static final double PLATFORM_KEEPS_A_YEAR = .915;

    /** A well's nameplate is a hundred barrels a day (BuildingManager, FUEL: 415 t a month at 7.33 barrels a tonne). */
    public static final double NAMEPLATE_BARRELS_A_DAY = 100;

    /** A well lifting under ten barrels a day is worn out and retired (the research's 3.2): 41.5 t a month of a 415 t well's. */
    public static final double WORN_OUT_BARRELS_A_DAY = 10;

    /** ...the share of its nameplate a well is retired under: WORN_OUT_BARRELS_A_DAY over NAMEPLATE_BARRELS_A_DAY, a tenth. */
    public static final double WORN_OUT_SHARE = WORN_OUT_BARRELS_A_DAY / NAMEPLATE_BARRELS_A_DAY;

    /** The extras' key a vintage is saved under: vintages.<month opened>.<kind>, its count the value. */
    public static final String VINTAGE_KEY = "vintages.";

    /**
     * The share of its nameplate a well of `kind` lifts at `age` months (pure):
     * a land well LAND_KEEPS_A_YEAR to the power of its years; a platform well
     * 1 through its plateau and PLATFORM_KEEPS_A_YEAR to the power of its
     * years past it. StrictMath, as World's arithmetic, so a well declines the
     * same on every machine. An age under nothing is read as nothing.
     */
    public static double profile(WellKind kind, int age) {
        int a = Math.max(0, age);
        if (kind == WellKind.PLATFORM) {
            return a < PLATFORM_PLATEAU_MONTHS ? 1
                    : StrictMath.pow(PLATFORM_KEEPS_A_YEAR, (a - PLATFORM_PLATEAU_MONTHS) / 12.0);
        }
        return StrictMath.pow(LAND_KEEPS_A_YEAR, a / 12.0);
    }

    /** The age, in months, from which a well of `kind` lifts under WORN_OUT_SHARE of its nameplate: 263 on land, 348 on a platform. */
    public static int lifeMonths(WellKind kind) {
        return kind == WellKind.PLATFORM ? PLATFORM_LIFE : LAND_LIFE;
    }

    private static final int LAND_LIFE = firstWornOut(WellKind.LAND), PLATFORM_LIFE = firstWornOut(WellKind.PLATFORM);

    private static int firstWornOut(WellKind kind) {
        int age = 0;
        while (!(profile(kind, age) < WORN_OUT_SHARE)) age++;
        return age;
    }

    /** The vintages as struck, oldest first (the month's reading is vintagesNow()). */
    private final List<Vintage> vintages = new ArrayList<>();

    /** Moves with every change to `vintages`, for the lift's cache. */
    private long vintagesStamp;

    /** The vintages as struck last, oldest first: what the save carries. */
    public List<Vintage> vintages() {
        return List.copyOf(vintages);
    }

    /** Whether a building is a land well: one of this sector's MINING templates that lifts crude, and not a platform's well (0.7.91). */
    public static boolean isLandWell(BuildingsTemplate t) {
        return t != null && t.getCategory() == BuildingType.MINING && t.makes(Good.CRUDE) > 0 && !t.isPlatformWell();
    }

    /** Whether a building is a well of either kind: a land well, or a platform's (0.7.91). */
    public static boolean isWell(BuildingsTemplate t) {
        return isLandWell(t) || (t != null && t.isPlatformWell() && t.makes(Good.CRUDE) > 0);
    }

    /** The land wells standing, finished. */
    public int landWellsStanding() {
        if (buildings == null) return 0;
        int n = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (isLandWell(t)) n += buildings.getQuantity(t.getId());
        }
        return n;
    }

    /** A land well's nameplate a month: the template's crude (415 t); 0 with no such template. */
    public double wellNameplate() {
        if (buildings == null) return 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (isLandWell(t)) return t.makes(Good.CRUDE);
        }
        return 0;
    }

    /** The month the vintages are read at: the game's. */
    private int monthNow() {
        return game == null ? 0 : game.getMonth();
    }

    /**
     * Vintages read against the wells standing (pure): of `kind`, those it
     * holds past the wells standing are dropped oldest first, and wells
     * standing past those it holds are a new vintage opened in `month`;
     * vintages of the other kind are left as they are.
     */
    public static List<Vintage> view(List<Vintage> kept, WellKind kind, int standing, int month) {
        int held = 0;
        for (Vintage v : kept) if (v.kind() == kind) held += v.count();
        List<Vintage> out = new ArrayList<>(kept.size() + 1);
        int drop = Math.max(0, held - Math.max(0, standing));
        for (Vintage v : kept) {
            if (v.kind() != kind || drop == 0) {
                out.add(v);
                continue;
            }
            int gone = Math.min(drop, v.count());
            drop -= gone;
            if (v.count() > gone) out.add(new Vintage(v.month(), v.count() - gone, kind));
        }
        int added = Math.max(0, standing - held);
        if (added > 0) {
            Vintage last = out.isEmpty() ? null : out.get(out.size() - 1);
            if (last != null && last.month() == month && last.kind() == kind) {
                out.set(out.size() - 1, new Vintage(month, last.count() + added, kind));
            } else {
                out.add(new Vintage(month, added, kind));
            }
        }
        return out;
    }

    /**
     * The vintages this month, read against the wells standing (view()): what
     * the lift and the retirements read - the land wells, and since 0.7.91 the
     * platform wells in a platform's slots (THE OIL AT SEA). A city with no
     * platform reads exactly the land wells' view.
     */
    public List<Vintage> vintagesNow() {
        return vintagesFor(landWellsStanding(), platformWellsInSlots(), monthNow());
    }

    /** The vintages read against `land` land wells and `platform` platform wells in slots, in `month`: the land's view, then the platforms'. */
    private List<Vintage> vintagesFor(int land, int platform, int month) {
        return view(view(vintages, WellKind.LAND, land, month), WellKind.PLATFORM, platform, month);
    }

    /** Strikes the vintages to this month's reading (endOfMonth(): once a month, after it has lifted). */
    public void strikeVintages() {
        List<Vintage> now = vintagesNow();
        if (now.equals(vintages)) return;
        vintages.clear();
        vintages.addAll(now);
        vintagesStamp++;
    }

    /** A fixture's vintages, struck as given, oldest first (WellCheck, SaveFileCheck). */
    public void setVintagesForTest(List<Vintage> given) {
        vintages.clear();
        vintages.addAll(given);
        vintagesStamp++;
    }

    /** The lift's cache: the month, the wells standing (land, and platform in slots) and the vintages' stamp it was summed for. */
    private int liftMonth = Integer.MIN_VALUE, liftStanding = -1, liftAtSea = -1;
    private long liftStamp = -1;
    private double liftCached, liftAtSeaCached;

    /**
     * The crude the wells can lift a month (0.7.84): over this month's
     * vintages, each vintage's wells times a well's nameplate times its kind's
     * profile at its age, oldest first - cached on the month, the wells
     * standing and the vintages. Every other good, and a sector with no city,
     * the template's nameplate. Since 0.7.91 the platform wells in slots too,
     * each at its own template's nameplate (THE OIL AT SEA).
     */
    @Override
    public double getCapacity(Good g) {
        if (g != Good.CRUDE || game == null || buildings == null) return super.getCapacity(g);
        strikeLift();
        return liftCached;
    }

    /** The part of getCapacity(CRUDE) the platform wells lift (0.7.91): what the shuttle tankers and the pipelines carry. */
    public double getCapacityAtSea() {
        if (game == null || buildings == null) return 0;
        strikeLift();
        return liftAtSeaCached;
    }

    /** Sums the month's lift into the cache, if the month, the wells or the vintages moved. */
    private void strikeLift() {
        int month = monthNow(), standing = landWellsStanding(), atSea = platformWellsInSlots();
        if (month == liftMonth && standing == liftStanding && atSea == liftAtSea && vintagesStamp == liftStamp) return;
        double each = wellNameplate(), eachAtSea = platformWellNameplate(), lift = 0, sea = 0;
        for (Vintage v : vintagesFor(standing, atSea, month)) {
            if (v.kind() == WellKind.PLATFORM) {
                double part = v.count() * eachAtSea * profile(v.kind(), month - v.month());
                lift += part;
                sea += part;
            } else {
                lift += v.count() * each * profile(v.kind(), month - v.month());
            }
        }
        liftMonth = month;
        liftStanding = standing;
        liftAtSea = atSea;
        liftStamp = vintagesStamp;
        liftCached = lift;
        liftAtSeaCached = sea;
    }

    /** The wells' nameplate at the template's figure, as if every one were new: what the decline is measured against. */
    public double newWellsCapacity() {
        return super.getCapacity(Good.CRUDE);
    }

    /** The wells standing that are worn out this month: past their kind's life (lifeMonths()), under ten barrels a day. */
    public int wornOut() {
        int n = 0, month = monthNow();
        for (Vintage v : vintagesNow()) {
            if (month - v.month() >= lifeMonths(v.kind())) n += v.count();
        }
        return n;
    }

    /** ...of one kind (0.7.91): the land wells at 263 months, the platform wells at 348. */
    public int wornOut(WellKind kind) {
        int n = 0, month = monthNow();
        for (Vintage v : vintagesNow()) {
            if (v.kind() == kind && month - v.month() >= lifeMonths(v.kind())) n += v.count();
        }
        return n;
    }

    /** The land well the sector drills, its own template: what a worn-out well's retirement sells. */
    public BuildingsTemplate landWell() {
        if (buildings == null) return null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) if (isLandWell(t)) return t;
        return null;
    }

    /** The month's loose ends (Markets.clearMonth()'s last step): the shuttle tankers paid for the month's crude ashore (0.7.91), the platforms and pipelines struck to what stood, and the vintages struck to the wells that stood this month. */
    @Override
    public void endOfMonth(Game game) {
        if (game == null) return;
        payShuttle();
        strikeOffshore();
        strikeVintages();
    }

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        for (Vintage v : vintages) extras.put(VINTAGE_KEY + v.month() + "." + v.kind().name(), (double) v.count());
        for (int i = 0; i < platforms.size(); i++) {
            Platform p = platforms.get(i);
            extras.put(PLATFORM_KEY + i + "." + p.cell() + "." + p.index() + "." + p.month(), (double) p.wells());
        }
        for (int i = 0; i < pipelines.size(); i++) {
            Pipeline p = pipelines.get(i);
            extras.put(PIPELINE_KEY + i + "." + p.cell() + "." + p.index() + "." + p.month(), (double) p.km());
        }
    }

    /** A save from before 0.7.84 has none: its wells are read as new (view()). A key or count that does not read is skipped. */
    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        vintages.clear();
        for (Map.Entry<String, Double> e : extras.entrySet()) {
            String k = e.getKey();
            Double c = e.getValue();
            if (k == null || !k.startsWith(VINTAGE_KEY) || c == null || !(c >= 1) || !Double.isFinite(c)) continue;
            String[] parts = k.substring(VINTAGE_KEY.length()).split("\\.");
            if (parts.length != 2) continue;
            try {
                vintages.add(new Vintage(Integer.parseInt(parts[0]), (int) Math.min(Integer.MAX_VALUE, Math.floor(c)),
                        WellKind.valueOf(parts[1])));
            } catch (IllegalArgumentException bad) {
                // not a vintage this build reads
            }
        }
        vintages.sort(java.util.Comparator.comparingInt(Vintage::month).thenComparing(Vintage::kind));
        vintagesStamp++;
        restoreOffshore(extras);
    }

    /** The platforms and pipelines out of a save's extras (0.7.91): by their place in the list; a save from before them has none, and a key that does not read is skipped. */
    private void restoreOffshore(Map<String, Double> extras) {
        java.util.TreeMap<Integer, Platform> ps = new java.util.TreeMap<>();
        java.util.TreeMap<Integer, Pipeline> ls = new java.util.TreeMap<>();
        for (Map.Entry<String, Double> e : extras.entrySet()) {
            String k = e.getKey();
            Double v = e.getValue();
            if (k == null || v == null || !Double.isFinite(v) || !(v >= 0)) continue;
            boolean platform = k.startsWith(PLATFORM_KEY), pipe = k.startsWith(PIPELINE_KEY);
            if (!platform && !pipe) continue;
            String[] parts = k.substring((platform ? PLATFORM_KEY : PIPELINE_KEY).length()).split("\\.");
            if (parts.length != 4) continue;
            try {
                int at = Integer.parseInt(parts[0]), cell = Integer.parseInt(parts[1]), index = Integer.parseInt(parts[2]),
                        month = Integer.parseInt(parts[3]), n = (int) Math.min(Integer.MAX_VALUE, Math.floor(v));
                if (platform) ps.put(at, new Platform(cell, index, n, month));
                else ls.put(at, new Pipeline(cell, index, n, month));
            } catch (NumberFormatException bad) {
                // not a record this build reads
            }
        }
        platforms.clear();
        platforms.addAll(ps.values());
        pipelines.clear();
        pipelines.addAll(ls.values());
        offshoreStamp++;
    }

    @Override
    protected void resetExtras() {
        vintages.clear();
        vintagesStamp++;
        platforms.clear();
        pipelines.clear();
        offshoreStamp++;
    }

    /**
     * Whether to drill another well: one a month while an owned oil site is
     * unworked and oil is left in the ground - Mining's rule, on oil
     * (spec-land 2.7). The deposit word says "deposit", which the Sectors
     * screen reads as ORE (BuildCard.wordKind()). Since 0.7.84 a land well's
     * site is a dry one (Game.sitesFor()): the sea's sites are a
     * platform's (since 0.7.91), and the land wells already on them are kept.
     *
     * THE THREE KINDS AT SEA (0.7.91, batch O10; spec-oil 2.7, 2.11): when
     * no land well is ordered, a platform well into a free slot, then a
     * field's crude pipeline where the rule says it pays, then a platform's
     * jacket on the shallow field with the most sea sites free (planAtSea()).
     * When nothing at sea is ordered the land's decision stands, its words
     * and its "no land" with it (★: the sea's refusal is the Oil page's,
     * atSeaWord()).
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }
        BusinessInvestment.Decision onLand = planOnLand(plans, game);
        if (onLand.build) return onLand;
        BusinessInvestment.Decision atSea = planAtSea(plans, game);
        atSeaWord = atSea == null || atSea.build ? null : atSea.reason;
        return atSea != null && atSea.build ? atSea : onLand;
    }

    /** Why nothing at sea was ordered the last month the planner looked (null when it ordered, or had nothing at sea to weigh): the Oil page's line. Not saved: a screen's. */
    private String atSeaWord;

    public String atSeaWord() { return atSeaWord; }

    /** The land well's order, or why not: Mining's rule on the dry sites (until 0.7.91 the whole of plan(); WellCheck 5 reads it). */
    public BusinessInvestment.Decision planOnLand(BusinessInvestment plans, Game game) {

        String sector = key();
        LandManager land = game.getLandManager();
        int dry = land.getSites(Resource.OIL, true);
        int unworked = dry - game.landWellsCommitted();
        double reserve = land.getOilReserveTonnes();

        if (land.getOilSites() <= 0) return BusinessInvestment.Decision.no(sector, "no oil deposit to drill");
        if (unworked <= 0) {
            return BusinessInvestment.Decision.no(sector, dry < land.getOilSites()
                    ? "no oil deposit on dry ground to drill" : "no oil deposit to drill");
        }
        if (reserve <= 0)  return BusinessInvestment.Decision.no(sector, "every oil deposit is worked out");

        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.makes(Good.CRUDE) <= 0 || !isLandWell(t)) continue;
            // ...and one the city could staff (0.7.18; see Sector.staffing()).
            Staffing staffing = staffing(t);
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = estimatedMonthlyProfit(t, plans) / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            return BusinessInvestment.Decision.no(sector, staffingHold != null
                    ? staffingHold.why(staffingHoldName) : "nothing worth sinking");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%d oil deposit site(s) undrilled, %,.0fk tonnes in the ground",
                        unworked, reserve / 1000), true);
    }

    /**
     * A price-taking exporter sells what it lifts and shrinks on distress -
     * while there is oil. A well over a worked-out field lifts nothing, and is
     * spare by any measure: Mining's rule, for Mining's reason.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null) return null;
        double capacity = getCapacity(Good.CRUDE);
        if (capacity <= 0) return null;
        return game.getLandManager().getOilReserveTonnes() > 0
                ? null
                : new double[] { 0, capacity };
    }

    /**
     * A well's part of the measure above (0.7.84): the average well's lift -
     * the template's nameplate times the wells' lift over what they would lift
     * new - so a field worked out sheds wells by the count, as it did before
     * the wells declined, not by their lift in new wells. A platform's well
     * the same (0.7.91).
     */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        double each = super.unitsOf(t), asNew = newWellsCapacity();
        if (!isWell(t) || game == null || !(asNew > 0)) return each;
        return each * (getCapacity(Good.CRUDE) / asNew);
    }

    /**
     * Whether a holding may be sold back this month (0.7.91): a platform's
     * jacket and a pipeline are not plant the lift measures, so the
     * shrinking rules never pick them while they stand - they go when their
     * wells have (Game.retireWornOutWells(), THE OIL AT SEA's last
     * paragraph). Every well, as before.
     */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        if (t != null && (t.isPlatform() || t.isPipeline()) && buildings != null && buildings.getQuantity(t.getId()) > 0) return false;
        return true;
    }

    /* =====================================================================
       THE OIL AT SEA (0.7.91, batch O10; runs/spec-oil.md 2.7 and 2.11, the
       project's oil-and-ports-research.md 3.2 and 4.5; Jerus's answer B)

       Since 0.7.84 a land well stood only on a dry site, and a field whose
       centre is in the sea waited. The research's platform - 18,000 b/d,
       D$500M - would drain the largest shallow field near a generated city
       in nineteen months (spec-oil 2.7: 16 sites, 1.42 Mt), so the platform
       is built from the research's per-well figures (Jerus: "yes").

       THE JACKET. An Offshore Platform stands on a field the city owns whose
       centre lies in the sea no deeper than PLATFORM_MAX_DEPTH_M
       (LandManager.seaFields(), World.depthAt()), with slots for its
       template's wells - min(12, the field's sea sites its other platforms
       have not slotted). The record (platforms) keeps each standing jacket's
       field by its world cell and index, the wells in its slots and the
       month it opened, read against the jackets standing like the vintages:
       a new one stands on the shallow field with the most sea sites free,
       the nearest the founding site on a tie (freeField()), one gone is the
       newest. Wells are dealt to the slots oldest platform first; a well
       past every slot stands idle.

       THE WELLS. A Platform Well lifts its template's 415 t a month on the
       platform profile (THE WELLS DECLINE), in a free slot on a free sea
       site; the land wells keep the dry sites and the legacy wells standing
       on the sea's are counted against the sea's first.

       THE SHUTTLE TANKERS. Platform crude sold at home pays CRUDE's band
       freight a tonne - the lorries' freight as the boundary carries it,
       baseFreight x freightFactor x the rate (★ ashore costs what the
       boundary costs) - booked as a service bought from the world
       (Sector.bookImportedService(), "Shuttle tankers"). Crude shipped
       abroad pays none: the export price is the boundary's. The platform
       crude is its share of the month's lift, and its home part the share
       of the lift sold at home (payShuttle()).

       THE PIPELINE, counted in kilometres, a straight line from the field to
       the founding site, the template's cash a kilometre at sea and its
       "onshore" one on land. Built for a field when (shuttle freight less the
       pipe's repairs and property tax, a tonne) x its crude ashore a month x
       min(the oil left / the lift, PIPE_LIFE_MONTHS) is PIPE_PAYBACK times
       its cost, while the refiners are not losing money and the field's
       wells are on their plateau (pipelinePays()). A field whose whole pipe
       stands pays no shuttle on its crude. On land no pipe passes: the model
       has no domestic freight, so it saves nothing (the rule is written for
       both).

       THE PLANNER, after the land well: a platform well into a free slot,
       then a field's pipe that pays, then a jacket on the field with the most
       sea sites free, judged as a package with min(12, those sites) wells by
       the investors' interest test (estimatedMonthlyProfit(): the jacket's
       share of the package's earnings by cost, as a crude unit's is).

       DECOMMISSIONED: a jacket holding no well and the pipelines are not the
       lift's plant (mayRetire()); once the city's oil is worked out they are
       retired at the top of the month with the worn-out wells.
       ===================================================================== */

    /** The deepest sea a platform's jacket stands in, in metres: 150, the fixed platform's limit (the research's 3.2; spec-oil 2.7). */
    public static final double PLATFORM_MAX_DEPTH_M = 150;

    /** A pipe's working life, in months: 480, 40 years (the research's 4.5, est.) - what the pipeline rule counts the field's months to at most. */
    public static final int PIPE_LIFE_MONTHS = 480;

    /** The pipeline rule's margin: the freight a pipe saves over the field's life must repay its cost 1.25 times (the research's 4.5; spec-oil 2.11). */
    public static final double PIPE_PAYBACK = 1.25;

    /** The shuttle tankers' name on the Oil sector's input line. */
    public static final String SHUTTLE_TANKERS = "Shuttle tankers";

    /** The extras' key a platform is saved under: platforms.<place>.<cell>.<index>.<month opened>, the wells in its slots the value. */
    public static final String PLATFORM_KEY = "platforms.";

    /** ...and a pipeline: pipelines.<place>.<cell>.<index>.<month ordered>, its kilometres the value. */
    public static final String PIPELINE_KEY = "pipelines.";

    /** A platform's jacket as it stands: its field (the world cell and index; -1 when it stands on none), the wells in its slots, and the month it opened. */
    public record Platform(int cell, int index, int wells, int month) { }

    /** A field's crude pipeline: its field, the kilometres ordered for it, and the month the first was ordered. */
    public record Pipeline(int cell, int index, int km, int month) { }

    /** The platforms as struck last, in the order they opened. */
    private final List<Platform> platforms = new ArrayList<>();

    /** The pipelines as struck last, in the order they were ordered. */
    private final List<Pipeline> pipelines = new ArrayList<>();

    /** Moves with every change to the two lists, for the reads' caches. */
    private long offshoreStamp;

    /** The field this month's planner ordered a pipe for: where the kilometres ordered this month go (pipelinesNow()); cleared at the strike. */
    private Platform pipeFor;

    /** The platforms as struck last (what the save carries). */
    public List<Platform> platforms() { return List.copyOf(platforms); }

    /** The pipelines as struck last. */
    public List<Pipeline> pipelines() { return List.copyOf(pipelines); }

    /** A fixture's platforms and pipelines, struck as given (WellCheck). */
    public void setOffshoreForTest(List<Platform> given, List<Pipeline> pipes) {
        platforms.clear();
        platforms.addAll(given);
        pipelines.clear();
        pipelines.addAll(pipes);
        offshoreStamp++;
    }

    /** The first of this sector's templates that `which` picks, or null. */
    private BuildingsTemplate templateWhere(java.util.function.Predicate<BuildingsTemplate> which) {
        if (buildings == null) return null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) if (which.test(t)) return t;
        return null;
    }

    /** The Offshore Platform's jacket, the Platform Well and the Crude Pipeline: this sector's templates at sea, or null. */
    public BuildingsTemplate platformTemplate()   { return templateWhere(BuildingsTemplate::isPlatform); }
    public BuildingsTemplate platformWell()       { return templateWhere(t -> t.isPlatformWell() && t.makes(Good.CRUDE) > 0); }
    public BuildingsTemplate pipelineTemplate()   { return templateWhere(BuildingsTemplate::isPipeline); }

    private int standing(BuildingsTemplate t) { return t == null || buildings == null ? 0 : buildings.getQuantity(t.getId()); }

    private int onSite(BuildingsTemplate t) {
        BuildingsStacks s = t == null || buildings == null ? null : buildings.getStack(t);
        return s == null ? 0 : s.getUnderConstruction();
    }

    /** The jackets standing, finished. */
    public int jacketsStanding() { return standing(platformTemplate()); }

    /** The platform wells standing, finished - in a slot or not. */
    public int platformWellsStanding() { return standing(platformWell()); }

    /** A platform well's nameplate a month: its template's crude (415 t); 0 with none. */
    public double platformWellNameplate() {
        BuildingsTemplate t = platformWell();
        return t == null ? 0 : t.makes(Good.CRUDE);
    }

    /** The wells a jacket holds: its template's slots (12); 0 with none. */
    public int slotsEach() {
        BuildingsTemplate t = platformTemplate();
        return t == null ? 0 : t.platformSlots();
    }

    /** The city's founding site, {x, y} in plots: where every pipe runs to. */
    private long[] site() {
        return game == null ? new long[] { 0, 0 } : new long[] { game.getCityLand().siteX(), game.getCityLand().siteY() };
    }

    /** The oil fields the city owns in the sea no deeper than PLATFORM_MAX_DEPTH_M at their centre, in LandManager.seaFields()'s order: what a jacket stands on. */
    public List<LandManager.SeaField> shallowFields() {
        if (game == null) return List.of();
        List<LandManager.SeaField> out = new ArrayList<>();
        for (LandManager.SeaField f : game.getLandManager().seaFields(Resource.OIL)) if (isShallow(f)) out.add(f);
        return out;
    }

    /** Whether a jacket may stand on a sea field (pure): in the sea, PLATFORM_MAX_DEPTH_M deep or less at its centre, with a site the city owns. */
    public static boolean isShallow(LandManager.SeaField f) {
        return f != null && f.depth() > 0 && f.depth() <= PLATFORM_MAX_DEPTH_M && f.sites() > 0;
    }

    /** A field's sites in the list, by its cell and index; 0 when the city owns none of it (or it is not a shallow sea field). */
    static int sitesOf(List<LandManager.SeaField> fields, int cell, int index) {
        for (LandManager.SeaField f : fields) if (f.field().cell() == cell && f.field().index() == index) return f.sites();
        return 0;
    }

    /** The field in the list by its cell and index, or null. */
    static Deposit fieldOf(List<LandManager.SeaField> fields, int cell, int index) {
        for (LandManager.SeaField f : fields) if (f.field().cell() == cell && f.field().index() == index) return f.field();
        return null;
    }

    /**
     * Each platform's slots, in the list's order (pure): min(slotsEach, its
     * field's sites less the slots of the platforms before it on the same
     * field), never under none - none for a platform on no field, or on one
     * the city no longer owns.
     */
    public static int[] slotsOf(List<Platform> ps, List<LandManager.SeaField> fields, int slotsEach) {
        int[] out = new int[ps.size()];
        Map<Long, Integer> used = new java.util.HashMap<>();
        for (int i = 0; i < ps.size(); i++) {
            Platform p = ps.get(i);
            if (p.cell() < 0) continue;
            long id = ((long) p.cell() << 32) | (p.index() & 0xffffffffL);
            int taken = used.getOrDefault(id, 0);
            int s = Math.max(0, Math.min(slotsEach, sitesOf(fields, p.cell(), p.index()) - taken));
            out[i] = s;
            used.put(id, taken + s);
        }
        return out;
    }

    /** Each field's sea sites no platform in the list has slotted, in the list of fields' order (pure). */
    public static int[] freeSites(List<Platform> ps, List<LandManager.SeaField> fields, int slotsEach) {
        int[] slots = slotsOf(ps, fields, slotsEach), free = new int[fields.size()];
        for (int f = 0; f < fields.size(); f++) {
            Deposit d = fields.get(f).field();
            int used = 0;
            for (int i = 0; i < ps.size(); i++) if (ps.get(i).cell() == d.cell() && ps.get(i).index() == d.index()) used += slots[i];
            free[f] = Math.max(0, fields.get(f).sites() - used);
        }
        return free;
    }

    /** The field a new jacket stands on (pure): the one with the most sea sites free, the nearest (x, y) on a tie, then the first listed; -1 for none free. */
    public static int freeField(List<Platform> ps, List<LandManager.SeaField> fields, int slotsEach, long x, long y) {
        int[] free = freeSites(ps, fields, slotsEach);
        int best = -1;
        double bestFar = Double.MAX_VALUE;
        for (int f = 0; f < fields.size(); f++) {
            if (free[f] <= 0) continue;
            Deposit d = fields.get(f).field();
            double far = Math.hypot(d.x() - x, d.y() - y);
            if (best < 0 || free[f] > free[best] || (free[f] == free[best] && far < bestFar)) {
                best = f;
                bestFar = far;
            }
        }
        return best;
    }

    /**
     * The platforms read against `jackets` standing and `wells` platform
     * wells (pure): those past the jackets dropped newest first, jackets past
     * them opened in `month` on freeField() (or on none), then the wells in
     * slots dealt oldest platform first - at most every slot's.
     */
    public static List<Platform> platformView(List<Platform> kept, int jackets, int wells, int month,
                                              List<LandManager.SeaField> fields, int slotsEach, long x, long y) {
        List<Platform> ps = new ArrayList<>(kept.subList(0, Math.min(kept.size(), Math.max(0, jackets))));
        while (ps.size() < jackets) {
            int f = freeField(ps, fields, slotsEach, x, y);
            Deposit d = f < 0 ? null : fields.get(f).field();
            ps.add(new Platform(d == null ? -1 : d.cell(), d == null ? -1 : d.index(), 0, month));
        }
        int[] slots = slotsOf(ps, fields, slotsEach);
        int left = Math.max(0, wells);
        List<Platform> out = new ArrayList<>(ps.size());
        for (int i = 0; i < ps.size(); i++) {
            int in = Math.min(left, slots[i]);
            left -= in;
            Platform p = ps.get(i);
            out.add(new Platform(p.cell(), p.index(), in, p.month()));
        }
        return out;
    }

    /** The platforms' cache: the month, the jackets and wells standing, the record's stamp and the fields it was read against. */
    private int pvMonth = Integer.MIN_VALUE, pvJackets = -1, pvWells = -1;
    private long pvStamp = -1;
    private Object pvFields;
    private List<Platform> pvCached = List.of();

    /** The platforms this month, read against the jackets and platform wells standing (platformView()): what the slots, the lift and the shuttle read. None with no jacket, without reading the ground. */
    public List<Platform> platformsNow() {
        int jackets = jacketsStanding();
        if (jackets <= 0 && platforms.isEmpty()) return List.of();
        int month = monthNow(), wells = platformWellsStanding();
        List<LandManager.SeaField> fields = shallowFields();
        if (month == pvMonth && jackets == pvJackets && wells == pvWells && offshoreStamp == pvStamp && sameFields(fields)) return pvCached;
        long[] at = site();
        pvCached = List.copyOf(platformView(platforms, jackets, wells, month, fields, slotsEach(), at[0], at[1]));
        pvMonth = month;
        pvJackets = jackets;
        pvWells = wells;
        pvStamp = offshoreStamp;
        pvFields = fields;
        return pvCached;
    }

    private boolean sameFields(List<LandManager.SeaField> fields) { return fields.equals(pvFields); }

    /** The slots of the platforms standing, all of them. */
    public int slotsStanding() {
        List<Platform> ps = platformsNow();
        if (ps.isEmpty()) return 0;
        int n = 0;
        for (int s : slotsOf(ps, shallowFields(), slotsEach())) n += s;
        return n;
    }

    /** The platform wells in a slot: those that lift. */
    public int platformWellsInSlots() {
        if (jacketsStanding() <= 0 && platforms.isEmpty()) return 0;
        int n = 0;
        for (Platform p : platformsNow()) n += p.wells();
        return n;
    }

    /** How many more jackets the shallow fields the city owns could take: each field's sea sites no standing platform has slotted, a jacket to every slotsEach of them or part. */
    public int platformRoom() {
        int each = slotsEach();
        if (each <= 0) return 0;
        List<LandManager.SeaField> fields = shallowFields();
        if (fields.isEmpty()) return 0;
        int n = 0;
        for (int free : freeSites(platformsNow(), fields, each)) n += (free + each - 1) / each;
        return n;
    }

    /** The jackets ordered and on site, not yet standing: what an order for another counts against platformRoom(). */
    public int jacketsOnSite() { return onSite(platformTemplate()); }

    /** The platform wells standing and on site: what an order for another counts against the slots. */
    public int platformWellsCommitted() {
        BuildingsTemplate t = platformWell();
        return standing(t) + onSite(t);
    }

    /** Strikes the platforms and pipelines to this month's reading (endOfMonth(): once a month, after the month's crude is paid ashore). */
    public void strikeOffshore() {
        List<Platform> ps = platformsNow();
        List<Pipeline> ls = pipelinesNow();
        if (!ps.equals(platforms) || !ls.equals(pipelines)) {
            platforms.clear();
            platforms.addAll(ps);
            pipelines.clear();
            pipelines.addAll(ls);
            offshoreStamp++;
        }
        pipeFor = null;
    }

    /* ----- the pipelines ----- */

    /** A field's pipe, in whole kilometres: the straight line from its centre to (x, y), rounded up - at least one. */
    public static int lengthKm(Deposit field, long x, long y) {
        double km = Math.hypot(field.x() - x, field.y() - y) * World.PLOT_M / 1000;
        return (int) Math.max(1, Math.ceil(km));
    }

    /** The kilometres of pipe standing, finished. */
    public int pipeKmStanding() { return standing(pipelineTemplate()); }

    /** ...and standing and on site: what the records hold. */
    public int pipeKmCommitted() {
        BuildingsTemplate t = pipelineTemplate();
        return standing(t) + onSite(t);
    }

    /**
     * The pipelines read against `km` kilometres committed (pure): those past
     * them taken from the newest record; kilometres past the records go to
     * `wanted`'s field (this month's order), else to the first platform field
     * in `ps` with no pipe, else to the last record, else to none, opened in
     * `month`.
     */
    public static List<Pipeline> pipelineView(List<Pipeline> kept, int km, Platform wanted, List<Platform> ps, int month) {
        List<Pipeline> out = new ArrayList<>(kept);
        int held = 0;
        for (Pipeline p : out) held += p.km();
        int drop = Math.max(0, held - Math.max(0, km));
        for (int i = out.size() - 1; i >= 0 && drop > 0; i--) {
            Pipeline p = out.get(i);
            int gone = Math.min(drop, p.km());
            drop -= gone;
            if (p.km() > gone) out.set(i, new Pipeline(p.cell(), p.index(), p.km() - gone, p.month()));
            else out.remove(i);
        }
        int added = Math.max(0, km - held);
        if (added <= 0) return out;
        int cell = -1, index = -1;
        if (wanted != null) {
            cell = wanted.cell();
            index = wanted.index();
        } else {
            for (Platform p : ps) {
                if (p.cell() < 0) continue;
                boolean piped = false;
                for (Pipeline l : out) piped |= l.cell() == p.cell() && l.index() == p.index();
                if (!piped) { cell = p.cell(); index = p.index(); break; }
            }
            if (cell < 0 && !out.isEmpty()) {
                Pipeline last = out.get(out.size() - 1);
                cell = last.cell();
                index = last.index();
            }
        }
        for (int i = 0; i < out.size(); i++) {
            Pipeline p = out.get(i);
            if (p.cell() == cell && p.index() == index) {
                out.set(i, new Pipeline(cell, index, p.km() + added, p.month()));
                return out;
            }
        }
        out.add(new Pipeline(cell, index, added, month));
        return out;
    }

    /** The pipelines this month, read against the kilometres committed (pipelineView()). None with no pipe, without reading anything else. */
    public List<Pipeline> pipelinesNow() {
        int km = pipeKmCommitted();
        if (km <= 0 && pipelines.isEmpty()) return List.of();
        return pipelineView(pipelines, km, pipeFor, platformsNow(), monthNow());
    }

    /**
     * The fields whose whole pipe stands (pure): the kilometres standing dealt
     * to the records oldest first, each field's pipe whole when its
     * kilometres reach its length (lengthKm()). Keyed cell << 32 | index.
     */
    public static java.util.Set<Long> pipedFields(List<Pipeline> ls, int kmStanding, List<LandManager.SeaField> fields, long x, long y) {
        java.util.Set<Long> out = new java.util.HashSet<>();
        int left = Math.max(0, kmStanding);
        for (Pipeline p : ls) {
            int in = Math.min(left, p.km());
            left -= in;
            Deposit d = p.cell() < 0 ? null : fieldOf(fields, p.cell(), p.index());
            if (d != null && in >= lengthKm(d, x, y)) out.add(((long) p.cell() << 32) | (p.index() & 0xffffffffL));
        }
        return out;
    }

    /** The share of the platform wells in slots whose field's whole pipe stands: the share of the crude at sea the shuttle tankers do not carry. */
    public double pipedShare() {
        int inSlots = platformWellsInSlots();
        if (inSlots <= 0 || pipeKmStanding() <= 0) return 0;
        long[] at = site();
        java.util.Set<Long> piped = pipedFields(pipelinesNow(), pipeKmStanding(), shallowFields(), at[0], at[1]);
        int n = 0;
        for (Platform p : platformsNow()) if (piped.contains(((long) p.cell() << 32) | (p.index() & 0xffffffffL))) n += p.wells();
        return n / (double) inSlots;
    }

    /* ----- the shuttle tankers ----- */

    /** What a tonne of crude costs to bring ashore by shuttle tanker this month: CRUDE's band freight, baseFreight x freightFactor x the rate (spec-oil 2.7). */
    public double shuttleFreightPerTonne() {
        GoodsMarket m = markets == null ? null : markets.get(Good.CRUDE);
        return m == null ? 0 : Good.CRUDE.baseFreight() * m.getFreightFactor() * m.getExchangeRate();
    }

    /** The month's shuttle tankers: tonnes brought ashore and what they cost (payShuttle()); not saved - the statement carries the bill. */
    private double shuttleTonnes, shuttleBill;

    public double getShuttleTonnes() { return shuttleTonnes; }
    public double getShuttleBill()   { return shuttleBill; }

    /**
     * Pays the shuttle tankers for the month's crude brought ashore
     * (endOfMonth(), after the crude's market cleared): the lift's share at
     * sea (getCapacityAtSea() over getCapacity()) of what was sold at home,
     * less the share a whole pipe carries, at shuttleFreightPerTonne() -
     * booked as a service bought from the world. Nothing with no platform
     * crude, or none sold at home.
     */
    public void payShuttle() {
        shuttleTonnes = shuttleBill = 0;
        double sea = getCapacityAtSea(), all = getCapacity(Good.CRUDE);
        if (!(sea > 0) || !(all > 0)) return;
        Output o = output(Good.CRUDE);
        double home = Math.max(0, Math.min(o.soldLocal, o.produced));
        if (!(home > 0)) return;
        double tonnes = home * (sea / all) * (1 - pipedShare());
        double bill = tonnes * shuttleFreightPerTonne();
        if (!(bill > 0)) return;
        shuttleTonnes = tonnes;
        shuttleBill = bill;
        bookImportedService(SHUTTLE_TANKERS, bill);
    }

    /** Last month's share of the wells' crude sold at home, by its money at home and abroad (the statement's): 0 with none sold. */
    public double homeShareLastMonth() {
        Sector.Split s = statement() == null ? null : statement().sold.get(Good.CRUDE);
        if (s == null || !(s.total() > 0)) return 0;
        return s.atHome / s.total();
    }

    /* ----- the pipeline rule ----- */

    /**
     * The pipeline rule (pure; spec-oil 2.11): (the freight a tonne the pipe
     * saves less its repairs and property tax a tonne) x the tonnes a month
     * it carries x min(the months the oil lasts, PIPE_LIFE_MONTHS) is at
     * least PIPE_PAYBACK times its cost.
     */
    public static boolean pipelinePays(double freightPerTonne, double standingPerMonth, double tonnesPerMonth,
                                       double monthsLeft, double cost) {
        if (!(tonnesPerMonth > 0) || !(cost > 0)) return false;
        double months = Math.min(Double.isFinite(monthsLeft) ? Math.max(0, monthsLeft) : PIPE_LIFE_MONTHS, PIPE_LIFE_MONTHS);
        return (freightPerTonne - standingPerMonth / tonnesPerMonth) * tonnesPerMonth * months >= cost * PIPE_PAYBACK;
    }

    /**
     * A field's pipe weighed (spec-oil 2.11): its kilometres still to order,
     * their cost, the freight a tonne it saves (the shuttle's at sea; none on
     * land), its repairs and tax a month, the tonnes a month it would carry
     * (the field's wells' share of the lift at sea, at last month's share
     * sold at home), the months the city's oil lasts at the lift, and the
     * two provisos - the refiners not losing money, the field's first
     * platform on its plateau.
     */
    public record PipeCase(Deposit field, int km, double cost, double freight, double standing, double tonnes,
                           double months, boolean refinersPay, boolean onPlateau, boolean pays) { }

    /** ...for a field the city owns a platform on; null with no pipeline template, or the field's whole pipe ordered. */
    public PipeCase pipeCase(LandManager.SeaField f, BusinessInvestment plans) {
        BuildingsTemplate pipe = pipelineTemplate();
        if (pipe == null || game == null || plans == null) return null;
        long[] at = site();
        Deposit d = f.field();
        int ordered = 0;
        for (Pipeline p : pipelinesNow()) if (p.cell() == d.cell() && p.index() == d.index()) ordered += p.km();
        int km = lengthKm(d, at[0], at[1]) - ordered;
        if (km <= 0) return null;
        boolean atSea = f.depth() > 0;
        double scale = atSea || !(pipe.getCashCost() > 0) ? 1 : pipe.onshoreCashPerKm() / pipe.getCashCost();
        double cost = plans.getCostOf(pipe, km) * scale;
        double standing = plans.standingCostOf(this, pipe) * km * scale;
        double freight = atSea ? shuttleFreightPerTonne() : 0;
        int wells = 0, first = Integer.MAX_VALUE;
        for (Platform p : platformsNow()) {
            if (p.cell() != d.cell() || p.index() != d.index()) continue;
            wells += p.wells();
            first = Math.min(first, p.month());
        }
        int inSlots = platformWellsInSlots();
        double tonnes = inSlots > 0 ? getCapacityAtSea() * wells / inSlots * homeShareLastMonth() : 0;
        double lift = getCapacity(Good.CRUDE), left = game.getLandManager().getOilReserveTonnes();
        double months = lift > 0 ? left / lift : PIPE_LIFE_MONTHS;
        boolean refiners = plans.getLossMonths(Sectors.REFINING) == 0;
        boolean plateau = first != Integer.MAX_VALUE && monthNow() - first < PLATFORM_PLATEAU_MONTHS;
        boolean pays = refiners && plateau && pipelinePays(freight, standing, tonnes, months, cost);
        return new PipeCase(d, km, cost, freight, standing, tonnes, months, refiners, plateau, pays);
    }

    /** The field whose pipe pays best, by its life's saving over its cost; null for none. */
    public PipeCase bestPipe(BusinessInvestment plans) {
        PipeCase best = null;
        double bestRatio = 0;
        for (LandManager.SeaField f : shallowFields()) {
            boolean platformed = false;
            for (Platform p : platformsNow()) platformed |= p.cell() == f.field().cell() && p.index() == f.field().index();
            if (!platformed) continue;
            PipeCase c = pipeCase(f, plans);
            if (c == null || !c.pays()) continue;
            double ratio = (c.freight() * c.tonnes() - c.standing()) * Math.min(c.months(), PIPE_LIFE_MONTHS) / c.cost();
            if (best == null || ratio > bestRatio) {
                best = c;
                bestRatio = ratio;
            }
        }
        return best;
    }

    /* ----- the planner's three kinds at sea ----- */

    /** A platform's jacket weighed with its wells (spec-oil 2.7): its field, the wells it is judged with, the package's earnings a month and its cost. */
    public record JacketCase(LandManager.SeaField field, int wells, double earns, double cost) { }

    /** ...on freeField(): min(slotsEach, its free sea sites) wells at a platform well's earnings, less the jacket's running and standing costs; null with no field free or no templates. */
    public JacketCase jacketCase(BusinessInvestment plans) {
        BuildingsTemplate jacket = platformTemplate(), well = platformWell();
        if (jacket == null || well == null || plans == null || game == null) return null;
        List<LandManager.SeaField> fields = shallowFields();
        if (fields.isEmpty()) return null;
        List<Platform> ps = platformsNow();
        long[] at = site();
        int f = freeField(ps, fields, slotsEach(), at[0], at[1]);
        if (f < 0) return null;
        int n = Math.min(slotsEach(), freeSites(ps, fields, slotsEach())[f]);
        double earns = n * estimatedMonthlyProfit(well, plans) - plans.runningCostOf(jacket) - plans.standingCostOf(this, jacket);
        double cost = plans.getCostOf(jacket, 1) + plans.getCostOf(well, n);
        return new JacketCase(fields.get(f), n, earns, cost);
    }

    /** The platform crude's shuttle freight a month on one more well's crude sold at home: its nameplate at the plants' rate, as much of it as the house forecast leaves room for at home (BusinessInvestment.estimatedMakerProfit()'s split), at the shuttle's freight, less a whole pipe's share. */
    private double shuttleOnOneMore(BuildingsTemplate well, BusinessInvestment plans) {
        GoodsMarket m = markets == null ? null : markets.get(Good.CRUDE);
        if (m == null) return 0;
        double units = well.makes(Good.CRUDE);
        double room = Math.max(0, plans.forecast(this, m) - getCapacity(Good.CRUDE) - getPipeline(Good.CRUDE));
        double atHome = Math.min(units, room);
        return atHome * shuttleFreightPerTonne() * (1 - pipedShare()) * BusinessInvestment.operatingRateOf(getOperatingRate());
    }

    /**
     * What one more building of the wells' would clear a month (0.7.91): a
     * platform well the house's maker estimate less the shuttle tankers on
     * its crude sold at home; a jacket its share by cost of its package's
     * (jacketCase()), so the investors' interest test on the jacket's own
     * cost is the package's on its; a kilometre of pipe its share of this
     * month's pipe's saving less its repairs and tax. A land well as ever.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || plans == null || !t.standsAtSea()) return super.estimatedMonthlyProfit(t, plans);
        if (t.isPlatformWell()) return super.estimatedMonthlyProfit(t, plans) - shuttleOnOneMore(t, plans);
        if (t.isPlatform()) {
            JacketCase c = jacketCase(plans);
            if (c == null || !(c.cost() > 0)) return super.estimatedMonthlyProfit(t, plans);
            return c.earns() * plans.getCostOf(t, 1) / c.cost();
        }
        PipeCase c = pipeFor != null ? pipeCaseOf(pipeFor, plans) : bestPipe(plans);
        if (c == null || c.km() <= 0) return super.estimatedMonthlyProfit(t, plans);
        return (c.freight() * c.tonnes() - c.standing()) / c.km();
    }

    /** The pipe case of the field a record names, or null when the city holds it no longer. */
    private PipeCase pipeCaseOf(Platform at, BusinessInvestment plans) {
        for (LandManager.SeaField f : shallowFields()) {
            if (f.field().cell() == at.cell() && f.field().index() == at.index()) return pipeCase(f, plans);
        }
        return null;
    }

    /** The order at sea, or null when there is nothing at sea to weigh (THE OIL AT SEA, the planner). */
    private BusinessInvestment.Decision planAtSea(BusinessInvestment plans, Game game) {
        String sector = key();
        LandManager land = game.getLandManager();
        double reserve = land.getOilReserveTonnes();
        if (!(reserve > 0) || (land.getSites(Resource.OIL, false) <= 0 && jacketsStanding() <= 0)) return null;

        // 1. A platform well into a free slot.
        BuildingsTemplate well = platformWell();
        int free = well == null ? 0 : game.sitesFor(well) - platformWellsCommitted();
        BusinessInvestment.Decision held = null;
        if (free > 0) {
            Staffing staffing = staffing(well);
            double cost = plans.getCostOf(well, 1);
            if (!staffing.passes()) {
                held = BusinessInvestment.Decision.no(sector, staffing.why(well.getName()));
            } else if (cost > 0 && estimatedMonthlyProfit(well, plans) / cost > 0) {
                return new BusinessInvestment.Decision(sector, well, 1,
                        String.format("%d platform slot(s) free at sea, %,.0fk tonnes in the ground", free, reserve / 1000), true);
            }
        }

        // 2. A field's pipe, where it pays.
        PipeCase pipe = bestPipe(plans);
        BuildingsTemplate pipeT = pipelineTemplate();
        if (pipe != null && pipeT != null) {
            pipeFor = new Platform(pipe.field().cell(), pipe.field().index(), 0, monthNow());
            return new BusinessInvestment.Decision(sector, pipeT, pipe.km(),
                    String.format("a %d km pipe ashore saves the shuttle tankers' freight on %,.0f t a month", pipe.km(), pipe.tonnes()), true);
        }

        // 3. A jacket on the shallow field with the most sea sites free, judged with its wells.
        BuildingsTemplate jacket = platformTemplate();
        JacketCase c = jacketCase(plans);
        if (jacket != null && c != null && game.hasDepositFor(jacket, 1)) {
            Staffing staffing = staffing(jacket);
            if (!staffing.passes()) return BusinessInvestment.Decision.no(sector, staffing.why(jacket.getName()));
            if (c.earns() > 0) {
                return new BusinessInvestment.Decision(sector, jacket, 1,
                        String.format("a shallow sea field with %d site(s) free, %.0f m deep: a platform and %d well(s)",
                                c.field().sites(), c.field().depth(), c.wells()), true);
            }
            return BusinessInvestment.Decision.no(sector, "a platform and its wells would not pay");
        }
        return held;
    }

    @Override
    public List<Line> ownLines(Game game) {
        List<Line> lines = new java.util.ArrayList<>();
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;
        LandManager land = game.getLandManager();
        int dry = land.getSites(Resource.OIL, true);
        lines.add(Line.head("What is under the ground"));
        int landWells = game.landWellsCommitted();
        lines.add(Line.of("Oil sites owned", f.count(land.getOilSites())));
        lines.add(Line.of("On dry ground", f.count(dry)));
        lines.add(Line.of("Being worked", f.count(landWells),
                landWells < dry ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("Crude in the ground", f.count(land.getOilReserveTonnes()) + " t"));
        double asNew = newWellsCapacity();
        if (asNew > 0) {
            lines.add(Line.of("Lifting a month", f.count(getCapacity(Good.CRUDE)) + " t, "
                    + Math.round(getCapacity(Good.CRUDE) / asNew * 100) + "% of new"));
        }
        if (landWells < dry && land.getOilReserveTonnes() > 0) {
            lines.add(Line.note("There is oil under this city that no well is lifting."));
        }
        List<LandManager.SeaField> shallow = shallowFields();
        if (dry < land.getOilSites()) {
            lines.add(Line.note(shallow.isEmpty() ? "A land well stands only on dry ground, not on the sites at sea."
                    : "A land well stands only on dry ground; the sites at sea take a platform's wells."));
        }
        // The oil at sea (0.7.91): only where the city owns a shallow sea field or stands a platform.
        if (shallow.isEmpty() && jacketsStanding() <= 0 && pipeKmStanding() <= 0) return lines;
        int sites = 0;
        for (LandManager.SeaField s : shallow) sites += s.sites();
        lines.add(Line.head("At sea"));
        lines.add(Line.of("Sea sites, 150 m or less", f.count(sites)));
        lines.add(Line.of("Platforms", f.count(jacketsStanding()) + ", " + f.count(slotsStanding()) + " slots"));
        lines.add(Line.of("Platform wells", f.count(platformWellsInSlots()) + " in slots",
                platformWellsInSlots() < slotsStanding() ? Line.Tone.WARN : Line.Tone.GOOD));
        Double shuttle = statement() == null ? null : statement().otherInputs.get(SHUTTLE_TANKERS);
        lines.add(Line.of("Shuttle tankers last month", f.amount(shuttle == null ? 0 : shuttle)));
        if (pipeKmStanding() > 0) {
            long[] at = site();
            int whole = pipedFields(pipelinesNow(), pipeKmStanding(), shallow, at[0], at[1]).size();
            lines.add(Line.of("Pipelines", f.count(pipeKmStanding()) + " km, " + f.count(whole) + " whole"));
        }
        if (atSeaWord != null) lines.add(Line.note("At sea: " + atSeaWord + "."));
        return lines;
    }
}
