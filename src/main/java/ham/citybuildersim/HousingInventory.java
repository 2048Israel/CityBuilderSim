package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Aggregate housing titles, occupants and listings over the physical stacks.
 * WHY (RES2): listings and titles partition existing homes; neither creates
 * structures. One equivalent service cohort per recipe is sufficient until
 * HO1 introduces differing obligations. No row depends on represented count.
 */
public final class HousingInventory {
    /** The founding places are an endowment, without saleable front doors. */
    public static final int FOUNDING_PLACES = 100;
    public enum Owner { REAL_ESTATE, HOUSEHOLD, ESTATE, OUTBOUND }
    public record Key(int recipe, String dwelling) { }
    public record Stock(Key key, HousingRecipe recipe, HousingRecipe.Dwelling dwelling,
                        long structures, long pipelineStructures) {
        public double homes() { return (double) structures * dwelling.count(); }
        public double pipeline() { return (double) pipelineStructures * dwelling.count(); }
    }
    public record Allocation(double structure, double land, double collateral) { }

    /** A saved class/owner cohort. RE's ownership is the physical residual. */
    public static final class Holding {
        public int recipe;
        public String dwelling;
        public Owner owner;
        public double owned, occupied, listedVacant, listedOccupied;
        public Holding() { }
        Holding(Key key, Owner owner) { recipe = key.recipe(); dwelling = key.dwelling(); this.owner = owner; }
        Holding copy() {
            Holding h = new Holding(new Key(recipe, dwelling), owner);
            h.owned = owned; h.occupied = occupied;
            h.listedVacant = listedVacant; h.listedOccupied = listedOccupied;
            return h;
        }
    }

    private final Supplier<List<BuildingsStacks>> stacks;
    private final LongSupplier endowments;
    private final Map<Key, Map<Owner, Holding>> holdings = new LinkedHashMap<>();

    HousingInventory(Supplier<List<BuildingsStacks>> stacks, LongSupplier endowments) {
        this.stacks = stacks; this.endowments = endowments;
    }

    /** Physical recipe/service cohorts, including sites; a read never caches or trades. */
    public List<Stock> stock() {
        List<Stock> result = new ArrayList<>();
        for (BuildingsStacks s : stacks.get()) {
            HousingRecipe r = s.getBuilding().getHousingRecipe();
            if (r == null) continue;
            for (HousingRecipe.Dwelling d : r.mix()) result.add(new Stock(
                    new Key(s.getBuilding().getId(), d.id()), r, d, s.getQuantity(), s.getUnderConstruction()));
        }
        if (result.size() > HousingRecipe.MAX_CLASSES) throw new IllegalStateException("Housing class budget exceeded");
        return List.copyOf(result);
    }

    private Stock stock(Key key) {
        return stock().stream().filter(s -> s.key().equals(key)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown housing class " + key));
    }
    private Holding held(Key key, Owner owner) {
        Map<Owner, Holding> row = holdings.get(key);
        return row == null ? null : row.get(owner);
    }
    private Holding writable(Key key, Owner owner) {
        return holdings.computeIfAbsent(key, k -> new LinkedHashMap<>())
                .computeIfAbsent(owner, o -> new Holding(key, o));
    }
    private static void quantity(double n) {
        if (!Double.isFinite(n) || n < 0) throw new IllegalArgumentException("Invalid housing quantity");
    }
    private static double tolerance(double n) { return Math.max(1e-9, Math.ulp(n) * 16); }

    public double owned(Key key, Owner owner) {
        if (owner == null) throw new IllegalArgumentException("Missing owner");
        double homes = stock(key).homes();
        if (owner != Owner.REAL_ESTATE) {
            Holding h = held(key, owner);
            return h == null ? 0 : h.owned;
        }
        for (Owner o : Owner.values()) if (o != Owner.REAL_ESTATE) {
            Holding h = held(key, o);
            if (h != null) homes -= h.owned;
        }
        return Math.max(0, homes);
    }
    public double occupied(Key key, Owner owner) {
        Holding h = held(key, owner);
        return h == null ? 0 : h.occupied;
    }
    public double vacant(Key key, Owner owner) { return Math.max(0, owned(key, owner) - occupied(key, owner)); }
    public double listed(Key key, Owner owner, boolean occupied) {
        Holding h = held(key, owner);
        return h == null ? 0 : occupied ? h.listedOccupied : h.listedVacant;
    }
    public long foundingCapacity() { return Math.multiplyExact(FOUNDING_PLACES, endowments.getAsLong()); }
    public long homes() {
        long total = 0;
        for (Stock s : stock()) total += s.structures() * s.dwelling().count();
        return total;
    }
    public long pipelineHomes() {
        long total = 0;
        for (Stock s : stock()) total += s.pipelineStructures() * s.dwelling().count();
        return total;
    }
    public long capacity() {
        long total = foundingCapacity();
        // Legacy mod templates can have a rounded door size: keep their declared capacity.
        for (BuildingsStacks s : stacks.get()) if (s.getBuilding().getHousingRecipe() != null)
            total += s.getQuantity() * s.getBuilding().getCapacity();
        return total;
    }
    /** The RE portfolio includes its sites, but never the founding endowment. */
    public double ownedCapacity(Owner owner, boolean includePipeline) {
        double total = 0;
        for (BuildingsStacks stack : stacks.get()) {
            BuildingsTemplate t = stack.getBuilding();
            HousingRecipe r = t.getHousingRecipe();
            if (r == null) continue;
            if (owner == Owner.REAL_ESTATE) {
                total += (stack.getQuantity() + (includePipeline ? stack.getUnderConstruction() : 0)) * (double) t.getCapacity();
                for (HousingRecipe.Dwelling d : r.mix()) for (Owner o : Owner.values()) if (o != owner)
                    total -= owned(new Key(t.getId(), d.id()), o) * d.capacity() * t.getCapacity() / r.capacity();
            } else for (HousingRecipe.Dwelling d : r.mix())
                total += owned(new Key(t.getId(), d.id()), owner) * d.capacity() * t.getCapacity() / r.capacity();
        }
        return total;
    }
    public long[] homesBySize() {
        int widest = 1;
        List<Stock> rows = stock();
        for (Stock s : rows) widest = Math.max(widest, s.dwelling().capacity());
        long[] result = new long[widest + 1];
        for (Stock s : rows) result[s.dwelling().capacity()] += s.structures() * s.dwelling().count();
        return result;
    }

    /** Explicit occupancy input; listings cannot exceed either occupied or vacant stock. */
    public void occupy(Key key, Owner owner, double amount) {
        quantity(amount);
        double own = owned(key, owner);
        Holding h = held(key, owner);
        if (amount > own || (h != null && (h.listedOccupied > amount || h.listedVacant > own - amount)))
            throw new IllegalArgumentException("Occupancy conflicts with stock/listings");
        writable(key, owner).occupied = amount;
        coalesce();
    }
    public void list(Key key, Owner owner, double vacant, double occupied) {
        quantity(vacant); quantity(occupied);
        if (vacant > vacant(key, owner) || occupied > occupied(key, owner))
            throw new IllegalArgumentException("Listing exceeds existing stock");
        Holding h = writable(key, owner);
        h.listedVacant = vacant; h.listedOccupied = occupied;
        coalesce();
    }

    /** Atomic title transfer, retaining the stated occupied quantity; no money or physical work. */
    public void transfer(Key key, Owner from, Owner to, double amount, double occupied) {
        quantity(amount); quantity(occupied);
        if (from == null || to == null || occupied > amount
                || occupied > occupied(key, from) || amount - occupied > vacant(key, from))
            throw new IllegalArgumentException("Transfer exceeds source holding");
        if (from == to || amount == 0) return;
        double source = owned(key, from), target = owned(key, to);
        Holding a = writable(key, from), b = writable(key, to);
        a.owned = from == Owner.REAL_ESTATE ? 0 : source - amount;
        b.owned = to == Owner.REAL_ESTATE ? 0 : target + amount;
        a.occupied -= occupied; b.occupied += occupied;
        // Accepted quantities leave the seller's offered subset; a buyer does not inherit an offer.
        a.listedOccupied = Math.min(a.listedOccupied, a.occupied);
        a.listedVacant = Math.min(a.listedVacant, Math.max(0, source - amount - a.occupied));
        coalesce();
    }

    /** Fixed title weights partition actual project books/collateral for a later funded sale. */
    public Allocation allocate(Key key, Owner owner, double amount,
                               double structure, double land, double collateral) {
        quantity(amount); quantity(structure); quantity(land); quantity(collateral);
        Stock s = stock(key);
        if (amount > owned(key, owner)) throw new IllegalArgumentException("Allocation exceeds title");
        double weight = s.structures() * s.recipe().costWeight();
        double fraction = weight > 0 ? amount * s.dwelling().costWeight() / weight : 0;
        return new Allocation(structure * fraction, land * fraction, collateral * fraction);
    }

    /** Legacy match input by size, distributed evenly across equivalent class doors.
     * BO1-H/RES3 replace the match, including the owner-family seeker group, at this seam.
     */
    public void match(double[] occupiedBySize) {
        long[] doors = homesBySize();
        for (Stock s : stock()) {
            int size = s.dwelling().capacity();
            double wanted = size < occupiedBySize.length ? occupiedBySize[size] : 0;
            quantity(wanted);
            double occupiedElsewhere = 0;
            for (Owner o : Owner.values()) if (o != Owner.REAL_ESTATE) occupiedElsewhere += occupied(s.key(), o);
            double amount = doors[size] > 0 ? wanted * s.homes() / doors[size] : 0;
            Holding h = writable(s.key(), Owner.REAL_ESTATE);
            h.occupied = Math.min(owned(s.key(), Owner.REAL_ESTATE), Math.max(0, amount - occupiedElsewhere));
            h.listedOccupied = Math.min(h.listedOccupied, h.occupied);
            h.listedVacant = Math.min(h.listedVacant, vacant(s.key(), Owner.REAL_ESTATE));
        }
        coalesce();
    }

    /** Physical removal cannot destroy a title already conveyed to another owner. */
    boolean mayRemove(BuildingsStacks stack, long amount) {
        HousingRecipe r = stack.getBuilding().getHousingRecipe();
        if (r == null) return true;
        for (HousingRecipe.Dwelling d : r.mix()) {
            Key key = new Key(stack.getBuilding().getId(), d.id());
            if (amount * (double) d.count() > owned(key, Owner.REAL_ESTATE)) return false;
        }
        return true;
    }
    void physicalChanged(BuildingsStacks stack) {
        if (stack.getBuilding().getHousingRecipe() == null) return;
        for (HousingRecipe.Dwelling d : stack.getBuilding().getHousingRecipe().mix()) {
            Key key = new Key(stack.getBuilding().getId(), d.id());
            Holding h = held(key, Owner.REAL_ESTATE);
            if (h == null) continue;
            // The next match reassigns anonymous tenants after the existing retirement policy acts.
            h.occupied = Math.min(h.occupied, owned(key, Owner.REAL_ESTATE));
            h.listedOccupied = Math.min(h.listedOccupied, h.occupied);
            h.listedVacant = Math.min(h.listedVacant, vacant(key, Owner.REAL_ESTATE));
        }
        coalesce();
    }

    private void coalesce() {
        for (Map<Owner, Holding> row : holdings.values()) row.values().removeIf(h ->
                h.owned == 0 && h.occupied == 0 && h.listedVacant == 0 && h.listedOccupied == 0);
        holdings.values().removeIf(Map::isEmpty);
    }
    /** A new city clears assignments together with the physical stacks. */
    void clear() { holdings.clear(); }
    public List<Holding> forSave() {
        List<Holding> result = new ArrayList<>();
        for (Map<Owner, Holding> row : holdings.values()) for (Holding h : row.values()) result.add(h.copy());
        return result;
    }
    /** Validates the whole saved matrix before replacing it; no reconstruction of economic flows. */
    public void restore(List<Holding> saved) {
        HousingInventory candidate = new HousingInventory(stacks, endowments);
        if (saved == null) throw new IllegalArgumentException("Missing housing inventory");
        for (Holding h : saved) {
            Key key = new Key(h.recipe, h.dwelling);
            candidate.stock(key);
            quantity(h.owned); quantity(h.occupied); quantity(h.listedVacant); quantity(h.listedOccupied);
            if (h.owner == null || candidate.held(key, h.owner) != null
                    || (h.owner == Owner.REAL_ESTATE && h.owned != 0))
                throw new IllegalArgumentException("Invalid housing cohort");
            candidate.holdings.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(h.owner, h.copy());
        }
        for (Stock s : stock()) {
            double sum = 0;
            for (Owner o : Owner.values()) {
                Holding h = candidate.held(s.key(), o);
                if (h == null) continue;
                double own = candidate.owned(s.key(), o);
                if (o != Owner.REAL_ESTATE) sum += own;
                if (h.occupied > own + tolerance(own) || h.listedOccupied > h.occupied
                        || h.listedVacant > own - h.occupied + tolerance(own))
                    throw new IllegalArgumentException("Invalid housing subsets");
            }
            if (sum > s.homes() + tolerance(s.homes())) throw new IllegalArgumentException("Titles exceed stock");
        }
        holdings.clear(); holdings.putAll(candidate.holdings); coalesce();
    }
}
