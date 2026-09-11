package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;
import ham.citybuildersim.sectors.FoodIndustry;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Materials;
import ham.citybuildersim.sectors.Mining;
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
 * (Jerus: "a seventh sector").
 *
 * THE THREE UNUSUAL ONES have typed accessors, because the households pay
 * rent to one and buy groceries from another, and the city hands its build
 * orders to the third. Everything else reaches a sector by name.
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
            MATERIALS = "Materials";

    public static final String[] KEYS = {
        RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERIALS
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

    public Sectors(BuildingManager buildings, Markets markets) {
        retail = add(new Retail(), buildings, markets);
        realEstate = add(new RealEstate(), buildings, markets);
        industry = add(new FoodIndustry(), buildings, markets);
        construction = add(new Construction(), buildings, markets);
        heavyIndustry = add(new HeavyIndustry(), buildings, markets);
        mining = add(new Mining(), buildings, markets);
        materials = add(new Materials(), buildings, markets);

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
