package ham.citybuildersim;

import java.nio.file.Path;

/**
 * Scratch, not in the build (batch FX9): a kept playtest city's districts' own plans and MF1's rim, with the streets on bought
 * ground on or off (-Dfx9.on=true|false, DistrictPlan.streetsOnBought). Loads root/data/saves/slot-NN (a scratch copy).
 *
 *   java -Dfx9.on=false -cp "<classes>;<jars>;<this>" ham.citybuildersim.FX9Packed <root> <slot>
 */
public class FX9Packed {
    public static void main(String[] args) throws Exception {
        DistrictPlan.streetsOnBought = Boolean.parseBoolean(System.getProperty("fx9.on", "true"));
        Path root = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        Game g;
        try {
            g = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            g.run();
            g.loadGame(slot);
        } finally {
            System.setOut(real);
        }
        CityMap m = g.getCityMap();
        long placed = 0, over = 0, laid = 0, cells = 0, boxes = 0;
        int districts = 0;
        for (CityMap.District d : m.districts()) {
            DistrictPlan p = m.plan(d);
            districts++;
            placed += p.buildings;
            over += p.overflowCount();
            laid += p.boughtStreets;
            cells += p.boughtCells;
        }
        for (CityMap.District d : m.districts()) {
            if (m.planned(d)) continue;
            for (CityMap.Job j : m.planJobs(d)) { j.run(); m.adopt(j); }
        }
        m.packed();
        int[] pc = m.packedCounts();
        System.out.printf("month %d, streets on bought ground %s: %d districts, their own plans place %,d and leave %,d without a place;"
                + " %,d street plots laid on bought ground in %,d cell layings; packed at the rim %,d (smaller %,d, none %,d)%n",
                g.getMonth(), DistrictPlan.streetsOnBought ? "ON" : "OFF", districts, placed, over, laid, cells, pc[0], pc[1], pc[2]);
    }
}
