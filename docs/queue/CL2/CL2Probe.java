package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;

/** Scratch: every district of a copied city (or PlanCheck's fixture) planned - a digest of all plans, dead ends, buildings placed, cells by kind. Never writes a real slot. */
public class CL2Probe {
    public static void main(String[] args) throws Exception {
        CityMap m;
        String what;
        if (args[0].equals("city")) {
            Path root = Path.of(args[1]);
            int slot = Integer.parseInt(args[2]);
            GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
            PrintStream real = System.out;
            if (System.getProperty("loud") == null) System.setOut(PlanCheck.QUIET);
            Game g;
            try {
                g = new Game(files);
                g.run();
                g.loadGame(slot);
            } finally {
                System.setOut(real);
            }
            m = g.getCityMap();
            what = "city month " + g.getMonth();
        } else {
            BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
            long seed = Founding.DEFAULT_WORLD_SEED;
            long[] place = MapCheck.dryPlace(World.of(seed));
            m = PlanCheck.squareCity(types, seed, place, Double.parseDouble(args[1]));
            what = "fixture x " + args[1];
        }
        long digest = 0xcbf29ce484222325L;
        int dead = 0, placed = 0, overflow = 0, longCells = 0, squared = 0, blvd = 0, estate = 0, districts = 0;
        long squareStreets = 0;
        int[][] lay = new int[3][3];
        int undone = 0, refused = 0, merged = 0;
        double slowest = 0, totalMs = 0; int builds = 0;
        int n = DistrictPlan.FRAME;
        StringBuilder deadAt = new StringBuilder();
        for (CityMap.District d : m.districts()) {
            DistrictPlan.Input in = m.planInput(d);
            if (Boolean.getBoolean("comb")) in.combOnly = true;
            long t0 = System.nanoTime();
            DistrictPlan p = DistrictPlan.make(in);
            double ms = (System.nanoTime() - t0) / 1e6; totalMs += ms; builds += p.builds;
            slowest = Math.max(slowest, ms);
            districts++;
            digest = (digest ^ PlanCheck.placedDigest(p)) * 0x100000001b3L;
            digest = (digest ^ PlanCheck.streetDigest(p)) * 0x100000001b3L;
            placed += p.buildings;
            undone += p.mergesUndone;
            refused += p.mergesRefused;
            for (int v : p.overflow) overflow += v;
            for (int c = 0; c < DistrictPlan.CELLS; c++) {
                if (p.cellKind[c] == DistrictPlan.HOMES) {
                    lay[p.cellBoulevard[c] ? 2 : p.cellSquare[c] ? 1 : 0][p.cellLayout[c]]++;
                    if (p.cellMerged[c]) merged++;
                    if (p.cellBoulevard[c]) blvd++;
                    else if (p.cellSquare[c]) squared++;
                    else longCells++;
                } else if (p.cellKind[c] == DistrictPlan.ESTATE) estate++;
            }
            for (int y = 1; y < n - 1; y++) {
                for (int x = 1; x < n - 1; x++) {
                    int q = y * n + x;
                    if (p.kindAt(q) == DistrictPlan.NONE) continue;
                    int k = 0;
                    if (p.kindAt(q - 1) != DistrictPlan.NONE) k++;
                    if (p.kindAt(q + 1) != DistrictPlan.NONE) k++;
                    if (p.kindAt(q - n) != DistrictPlan.NONE) k++;
                    if (p.kindAt(q + n) != DistrictPlan.NONE) k++;
                    if (k <= 1) {
                        dead++;
                        int c = Math.min(x / DistrictPlan.CELL, 7) + Math.min(y / DistrictPlan.CELL, 7) * 8;
                        boolean fx = false, wt = false;
                        for (int yy = -3; yy <= 3; yy++) for (int xx = -3; xx <= 3; xx++) {
                            int a = (y + yy) * n + x + xx;
                            if (a < 0 || a >= n * n) continue;
                            if (in.fixed[a] != 0) fx = true;
                            if (in.terrain[a] == World.SALT || in.terrain[a] == World.FRESH || !in.owned[a]) wt = true;
                        }
                        if (deadAt.length() < 40000) deadAt.append(String.format(" (%d,%d)@%d,%d c%d%s%s%s L%d%s%s", d.dx, d.dy, x, y, c,
                                p.cellKind[c] == DistrictPlan.HOMES ? "H" : p.cellKind[c] == DistrictPlan.ESTATE ? "E" : "-",
                                p.cellSquare[c] ? "s" : "", p.cellMerged[c] ? "m" : "", p.cellLayout[c], fx ? " FX" : "", wt ? " WT" : ""));
                    }
                }
            }
        }
        System.out.printf("%s: %d districts, digest %d, dead ends %d, buildings %,d placed, %,d overflow; cells long %d squared %d boulevard %d estate %d; slowest plan %.1f ms%n",
                what, districts, digest, dead, placed, overflow, longCells, squared, blvd, estate, slowest);
        System.out.printf("layouts (today/dropped/brick): long %s squared %s boulevard %s; homes cells merged %d; merges undone %d refused %d%n", java.util.Arrays.toString(lay[0]), java.util.Arrays.toString(lay[1]), java.util.Arrays.toString(lay[2]), merged, undone, refused);
        System.out.printf("plans: %.0f ms in all, %d builds%n", totalMs, builds);
        if (args.length > 3 && args[3].equals("-v") || args.length > 2 && args[2].equals("-v")) System.out.println("dead ends at:" + deadAt);
    }
}
