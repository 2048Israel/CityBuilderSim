package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Scratch, not in the build (batch MF1, item 6): does CityRuns.keepYards() take a Rail Depot's yard back when a Rail Terminal
 * is added ahead of it in the asked sizes (Rail Terminals first, Rail Depots after), or taken away? The playtest never has a
 * Terminal (MF1YardProbe: its relays are the Depot itself sold and built again), so this lays the fixture by hand with
 * MainLineCheck's helpers.   java -cp target/classes;<jars> ham.citybuildersim.MF1YardLatent
 */
public class MF1YardLatent {
    public static void main(String[] args) {
        int[] term = MainLineCheck.terminalYard(), depot = MainLineCheck.depotYard();
        System.out.println("footprints: Rail Terminal " + Arrays.toString(term) + ", Rail Depot " + Arrays.toString(depot));
        JunctionCheck.PatchGround g = MainLineCheck.longGround();
        List<int[]> onlyDepot = new ArrayList<>(List.of(depot));
        CityRuns r = MainLineCheck.yardsRun(g, onlyDepot, 0);
        long[] d0 = r.yards().get(0);
        System.out.println("a Rail Depot alone: yard " + Arrays.toString(d0) + " asked " + Arrays.toString(r.yardAsked(0)));
        List<int[]> both = new ArrayList<>(List.of(term, depot));
        MainLineCheck.layYards(r, g, both, 0);
        System.out.println("a Rail Terminal added: " + r.yards().size() + " yards");
        for (int i = 0; i < r.yards().size(); i++) System.out.println("  yard " + i + " " + Arrays.toString(r.yards().get(i)) + " asked " + Arrays.toString(r.yardAsked(i)));
        System.out.println("  the Depot's old box still laid: " + r.yards().stream().anyMatch(y -> Arrays.equals(y, d0)));
        MainLineCheck.layYards(r, g, onlyDepot, 0);
        System.out.println("the Rail Terminal gone again: " + r.yards().size() + " yards, the Depot's at " + Arrays.toString(r.yards().get(0)) + " (first: " + Arrays.toString(d0) + ")");
    }
}
