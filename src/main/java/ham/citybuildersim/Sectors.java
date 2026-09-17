package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;
import ham.citybuildersim.sectors.Automotive;
import ham.citybuildersim.sectors.BusinessServices;
import ham.citybuildersim.sectors.Construction;
import ham.citybuildersim.sectors.FoodIndustry;
import ham.citybuildersim.sectors.FoodProcessing;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Manufacturing;
import ham.citybuildersim.sectors.Materials;
import ham.citybuildersim.sectors.Mining;
import ham.citybuildersim.sectors.Rail;
import ham.citybuildersim.sectors.RealEstate;
import ham.citybuildersim.sectors.Retail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every sector in the city, in one order, by one name.
 *
 * THE REGISTRY. A sector is added here and nowhere else: the credit desk,
 * the register, the exchange, the savings abroad, the tax policy, the VAT,
 * the books, the audit, the screens and the save all enumerate this list.
 * Until 2026-09-11 the same six were named in eleven loops in
 * EconomyManager, a six-way switch in SectorBooks, thirteen enumerations in
 * UserInterface and a hundred and ninety reaches in the harnesses - see
 * claude/the-sector-template.md.
 *
 * THE ORDER IS THE ORDER. Equity's company index, the households' share
 * arrays and the audit's pool list all follow it, so a sector goes on the
 * END like a BuildingType. The first six are the six the game had, in the
 * order BusinessDebtManager.SECTORS listed them; Materials is the seventh
 * (Jerus: "a seventh sector"), Business Services the eighth and Manufacturing
 * the ninth - the two whose customer is not in the city - and Agriculture the
 * tenth, which is the only one whose cost is the ground it stands on.
 *
 * THE UNUSUAL ONES have typed accessors, because the households pay rent to
 * one and buy groceries from another, the city hands its build orders to a
 * third, and the two export sectors each answer a question no other sector
 * can. Everything else reaches a sector by name.
 */
public final class Sectors {

    /**
     * The names, in the order, known before any instance exists - for the
     * things that size an array by the count at construction (Equity's
     * company list, the households' share cells) and cannot wait for a
     * registry to be built. The constructor checks the instances match.
     */
    public static final String RETAIL = "Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry",
            CONSTRUCTION = "Construction", HEAVY_INDUSTRY = "Heavy Industry", MINING = "Mining",
            MATERIALS = "Materials", BUSINESS_SERVICES = "Business Services",
            MANUFACTURING = "Manufacturing", AGRICULTURE = "Agriculture",
            FOOD_PROCESSING = "Food Processing", RAIL = "Rail",
            AUTOMOTIVE = "Automotive";

    /*
     * ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than
     * BuildingType's. Nothing is saved by this array's INDEX: SectorState is
     * keyed by name, Equity.COMPANIES is built from these strings and the
     * household cells carry the company names beside their holdings, so a save
     * written by the ten-sector build restores into the eleven-sector one with
     * the new sector simply empty. What order does decide is the order every
     * screen and every loop walks the sectors in, and appending keeps that
     * stable for anybody reading a saved run beside a live one.
     */
    public static final String[] KEYS = {
        RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERIALS,
        BUSINESS_SERVICES, MANUFACTURING, AGRICULTURE, FOOD_PROCESSING, RAIL, AUTOMOTIVE
    };

    private final List<Sector> all = new ArrayList<>();
    private final Map<String, Sector> byKey = new LinkedHashMap<>();

    private final Retail retail;
    private final RealEstate realEstate;
    private final FoodIndustry industry;
    private final Construction construction;
    private final HeavyIndustry heavyIndustry;
    private final Mining mining;
    private final Materials materials;
    private final BusinessServices businessServices;
    private final Manufacturing manufacturing;
    private final Agriculture agriculture;
    private final FoodProcessing foodProcessing;
    private final Rail rail;
    private final Automotive automotive;

    public Sectors(BuildingManager buildings, Markets markets) {
        retail = add(new Retail(), buildings, markets);
        realEstate = add(new RealEstate(), buildings, markets);
        industry = add(new FoodIndustry(), buildings, markets);
        construction = add(new Construction(), buildings, markets);
        heavyIndustry = add(new HeavyIndustry(), buildings, markets);
        mining = add(new Mining(), buildings, markets);
        materials = add(new Materials(), buildings, markets);
        businessServices = add(new BusinessServices(), buildings, markets);
        manufacturing = add(new Manufacturing(), buildings, markets);
        agriculture = add(new Agriculture(), buildings, markets);
        foodProcessing = add(new FoodProcessing(), buildings, markets);
        rail = add(new Rail(), buildings, markets);
        automotive = add(new Automotive(), buildings, markets);

        if (all.size() != KEYS.length) throw new IllegalStateException("Sectors.KEYS is out of step");
        for (int i = 0; i < KEYS.length; i++) {
            if (!all.get(i).key().equals(KEYS[i])) {
                throw new IllegalStateException("Sectors.KEYS[" + i + "] is " + KEYS[i]
                        + " but the registry has " + all.get(i).key());
            }
        }
    }

    private <T extends Sector> T add(T sector, BuildingManager buildings, Markets markets) {
        if (byKey.containsKey(sector.key())) {
            throw new IllegalStateException("two sectors named " + sector.key());
        }
        sector.attach(buildings, markets);
        all.add(sector);
        byKey.put(sector.key(), sector);
        return sector;
    }

    public List<Sector> all()   { return Collections.unmodifiableList(all); }
    public int size()           { return all.size(); }
    public Sector get(int i)    { return all.get(i); }

    /** The sector with this saved name, or null - a name from a save this build does not have loses that line, not the load. */
    public Sector byKey(String key) { return key == null ? null : byKey.get(key); }

    public int indexOf(String key) {
        for (int i = 0; i < all.size(); i++) if (all.get(i).key().equals(key)) return i;
        return -1;
    }

    /** The keys, in order. The same strings BusinessDebtManager, Equity and OutwardInvestment are keyed by. */
    public String[] keys() {
        String[] out = new String[all.size()];
        for (int i = 0; i < out.length; i++) out[i] = all.get(i).key();
        return out;
    }

    public Retail retail()               { return retail; }
    public RealEstate realEstate()       { return realEstate; }
    public FoodIndustry industry()       { return industry; }
    public Construction construction()   { return construction; }
    public HeavyIndustry heavyIndustry() { return heavyIndustry; }
    public Mining mining()               { return mining; }
    public Materials materials()         { return materials; }

    /**
     * Typed, unlike the rest of the ordinary six, because the labour market and
     * the playtest both want to ask it a question no other sector answers: what
     * share of what the world pays is going out in wages. See BusinessServices.
     */
    public BusinessServices businessServices() { return businessServices; }

    /**
     * Typed for the same reason, and for one more: it is the only sector that
     * BUYS a traded good another sector makes, so the mills' screen and the
     * playtest both want to ask it what it is paying for steel. See
     * Manufacturing.
     */
    public Manufacturing manufacturing() { return manufacturing; }

    /**
     * Typed, because the fields answer a question no other sector can: what
     * share of its own dinner the city grows, and what the ground under it is
     * costing. See Agriculture.
     */
    public Agriculture agriculture() { return agriculture; }

    /**
     * Typed, because it is the second sector that BUYS a traded good another
     * sector makes - the Livestock Farm's meat - so the farms' screen and the
     * playtest both want to ask it what it is paying for it. See FoodProcessing.
     */
    public FoodProcessing foodProcessing() { return foodProcessing; }

    /**
     * Typed, and for a reason none of the others have: it is the only sector
     * whose price is a fact about every OTHER sector's trade. Game hands it the
     * month at the top of the month, the income statements are billed from it,
     * the road network is relieved by it and the import and export bands move
     * with it. See sectors.Rail.
     */
    public Rail rail() { return rail; }

    /**
     * Typed, because it is the only sector that buys what Manufacturing makes,
     * and the mills' screen and the planner both want to ask what the parts are
     * costing. The same reason Manufacturing and Food Processing are typed.
     * See sectors.Automotive.
     */
    public Automotive automotive() { return automotive; }

    /** The city, handed to every sector once it exists. */
    public void attachGame(Game game) {
        for (Sector s : all) s.attachGame(game);
    }

    /** The sector that owns a building, or null for the city's own. */
    public Sector ownerOf(BuildingsTemplate t) {
        return t == null ? null : byKey(t.getSector());
    }

    /* ------------------------------ the loops ------------------------------ */

    public double totalCash() {
        double total = 0;
        for (Sector s : all) total += s.getCash();
        return total;
    }

    public double totalPayroll() {
        double total = 0;
        for (Sector s : all) total += s.getPayroll();
        return total;
    }

    public List<SectorState> toState() {
        List<SectorState> out = new ArrayList<>();
        for (Sector s : all) out.add(s.toState());
        return out;
    }

    public void restore(List<SectorState> saved) {
        if (saved == null) return;
        for (SectorState s : saved) {
            if (s == null) continue;
            Sector sector = byKey(s.key);
            if (sector != null) sector.restore(s);
        }
    }

    public void reset() {
        for (Sector s : all) s.reset();
    }

    public void redenominate(double scale) {
        for (Sector s : all) s.redenominate(scale);
    }
}
