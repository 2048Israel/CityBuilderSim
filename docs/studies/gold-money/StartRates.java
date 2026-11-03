package ham.citybuildersim;
import java.nio.file.Path;
/**
 * Which money, and at what rate, a city founded at seven spots gets at month 0 from today's world file (the gold-money study,
 * 2026-11-02): the money the nation used in the file's month, the rate at month 0 (January 1900, the stand-in century's), at month
 * 1,200 (January 2000, the file's first) and at month 1,511 (December 2025, its last).
 *
 * Compile and run from the repository root after a build (Git Bash), naming the world file:
 *   javac -d target/probe -cp "target/classes;$(cat target/classpath.txt)" docs/studies/gold-money/StartRates.java
 *   java -cp "target/probe;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.StartRates path/to/world-1.bin
 * Its output is kept beside it in startrates.txt.
 */
public final class StartRates {
    public static void main(String[] a) throws Exception {
        WorldData w = WorldData.open(Path.of(a[0]));
        System.out.println("stand-in months " + w.standInMonths());
        double[][] spots = {{35.68, 139.69}, {59.33, 18.07}, {52.37, 4.90}, {52.52, 13.40}, {51.51, -0.13}, {44.65, -63.57}, {40.71, -74.0}};
        String[] names = {"Tokyo", "Stockholm", "Amsterdam", "Berlin", "London", "Halifax", "New York"};
        for (int i = 0; i < spots.length; i++) {
            int n = w.ownerAt(spots[i][0], spots[i][1]);
            String m0 = n < 0 ? "?" : w.moneyAt(n, 0), mLast = n < 0 ? "?" : w.moneyAt(n, 1511);
            System.out.printf("%-10s nation %d money at month 0 %s, rate %.4f; month 1200 (2000-01) %.4f; month 1511 (2025-12) %s %.4f%n", names[i], n, m0,
                    w.rate(m0, 0), w.rate(m0, 1200), mLast, w.rate(mLast, 1511));
        }
    }
}
