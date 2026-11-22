package ham.citybuildersim;

import java.util.HashSet;
import java.util.List;

/**
 * The fixed dwelling mix and rights delivered by one residential structure.
 * WHY (RES2): a structure, a home and its title used to be the same capacity
 * counter. These are recipe classes, never individual homes or buildings.
 */
public record HousingRecipe(String form, List<Dwelling> mix, double sharedParking) {
    /** Initial catalogue budget, independent of the population represented. */
    public static final int MAX_CLASSES = 32;

    /** Fixed quantities per dwelling; weights allocate actual books, not new costs. */
    public record Dwelling(String id, int count, int capacity, double area,
                           int bedrooms, double quality, double privateParking,
                           double includedParking, double costWeight) {
        public Dwelling {
            if (id == null || id.isBlank() || count <= 0 || capacity <= 0
                    || bedrooms < 0 || !positive(area) || !positive(quality)
                    || !nonnegative(privateParking) || !nonnegative(includedParking)
                    || !positive(costWeight)) throw new IllegalArgumentException("Invalid dwelling metadata");
        }
    }

    public HousingRecipe {
        if (form == null || form.isBlank() || mix == null || mix.isEmpty()
                || mix.size() > MAX_CLASSES || !nonnegative(sharedParking))
            throw new IllegalArgumentException("Invalid housing recipe");
        mix = List.copyOf(mix);
        var ids = new HashSet<String>();
        double included = 0;
        for (Dwelling d : mix) {
            if (!ids.add(d.id())) throw new IllegalArgumentException("Duplicate dwelling class");
            included += d.count() * d.includedParking();
        }
        if (included > sharedParking) throw new IllegalArgumentException("Included rights exceed shared parking");
    }

    private static boolean positive(double n) { return Double.isFinite(n) && n > 0; }
    private static boolean nonnegative(double n) { return Double.isFinite(n) && n >= 0; }

    public long doors() { return mix.stream().mapToLong(Dwelling::count).sum(); }
    public long capacity() { return mix.stream().mapToLong(d -> (long) d.count() * d.capacity()).sum(); }
    public double costWeight() { return mix.stream().mapToDouble(d -> d.count() * d.costWeight()).sum(); }

    /** Compatibility metadata for hand-built/mod templates; no new form is activated. */
    public static HousingRecipe compatible(BuildingsTemplate t) {
        int doors = t.getDwellings() > 0 ? t.getDwellings() : Math.max(1, t.getCapacity() / 4);
        int size = Math.max(1, t.homeSize());
        return new HousingRecipe(t.getName(), List.of(new Dwelling("legacy", doors, size,
                size * 20.0, size <= 2 ? 0 : (size + 1) / 2, 1, 0, 0, size * 20.0)), 0);
    }
}
