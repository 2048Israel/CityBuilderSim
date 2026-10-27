package ham.citybuildersim;

import java.util.TreeSet;

/**
 * The ground a bridge is surveyed over, read from the World and the city's land: the Earth's terrain, relief and landmass a
 * tile at a time, the city's ownership from the land's grid, and the linked set from the books by landmass.
 *
 * WHY THIS EXISTS. BridgeSurvey is a function of a small interface so that the harness can hand it a grid of its own
 * making; this is the interface's one reader of the real thing (ET6, spec-earth2-terrain 6.3: the model reads World and
 * CityLand, never the map). It reads nothing it does not need: a tile's relief and landmass only where the city owns dry
 * ground in it, and the landmass of a plot the data names none for is home's, as Landmasses counts it.
 */
final class BridgeGround implements BridgeSurvey.Ground {

    private final World world;
    private final CityLand land;
    private final Landmasses landmasses;
    private final int[] ids = new int[World.TILE * World.TILE];
    private final int[] held = new int[World.TILE * World.TILE];

    BridgeGround(World world, CityLand land, Landmasses landmasses) {
        this.world = world;
        this.land = land;
        this.landmasses = landmasses;
    }

    @Override public int home() { return landmasses.home(); }

    @Override public int[] linkedIds() { return landmasses.linkedIds(); }

    @Override public long[] ownedTiles() {
        TreeSet<Long> tiles = new TreeSet<>();
        land.grid().leaves((level, x, y, holding) -> {
            long size = 1L << level;
            long tx0 = Math.floorDiv(x, World.TILE), tx1 = Math.floorDiv(x + size - 1, World.TILE);
            long ty0 = Math.floorDiv(y, World.TILE), ty1 = Math.floorDiv(y + size - 1, World.TILE);
            for (long ty = ty0; ty <= ty1; ty++) for (long tx = tx0; tx <= tx1; tx++) tiles.add(BridgeSurvey.key(tx, ty));
        });
        long[] out = new long[tiles.size()];
        int i = 0;
        for (long t : tiles) out[i++] = t;
        return out;
    }

    /** A tile can hold dry ground of a landmass off home only where the landmass layer has a cell of it within the reach of the tile (World.idsNear(): a window of cells, no plot read); home's ground can lie anywhere. */
    @Override public boolean mayHold(long tx, long ty, int landmass) {
        int home = landmasses.home();
        if (landmass == home || landmass == 0 || home == 0) return true;
        return java.util.Arrays.binarySearch(world.idsNear(tx, ty), landmass) >= 0;
    }

    @Override public void tile(long tx, long ty, BridgeSurvey.Tile out) {
        out.clear();
        world.tileTerrain(tx, ty, out.cover);
        LandGrid grid = land.grid();
        if (grid.cover(5, tx, ty) == LandGrid.NONE) return;
        grid.tileHoldings(tx, ty, held);
        boolean anyDry = false;
        for (int i = 0; i < held.length; i++) {
            out.owned[i] = held[i] >= 0;
            anyDry |= out.owned[i] && World.dry(out.cover[i]);
        }
        if (!anyDry) return;
        world.tileRelief(tx, ty, out.relief);
        world.tileLandmass(tx, ty, ids);
        int home = landmasses.home();
        for (int i = 0; i < ids.length; i++) out.landmass[i] = ids[i] == 0 ? home : ids[i];
    }

    @Override public double depthM(long x, long y) { return world.depthAt(x, y); }
}
