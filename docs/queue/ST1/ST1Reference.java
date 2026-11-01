package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Locale;

/**
 * ST1's fidelity check for ST1Probe: ScaleCheck section 6's town built by OrderSearchCheck.playtestCity() itself (not a replay of
 * it) to the month given, and its population, cash and homes printed at full precision. The probe's row for that month must read the
 * same to the last digit (run.sh label tree months, then the CSV's row; compare with `ref` below).
 *
 * Usage: java ... ham.citybuildersim.ST1Reference &lt;months&gt;
 */
public final class ST1Reference {

    private ST1Reference() { }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        int months = Integer.parseInt(args[0]);
        PrintStream real = System.out;
        PrintStream quiet = new PrintStream(new OutputStream() {
            @Override public void write(int b) { }
            @Override public void write(byte[] b, int off, int len) { }
        });
        Path root = TempDirs.create("st1ref");
        GameFiles grown = new GameFiles(root.resolve("grown"), root.resolve("grown-no-legacy"));
        Game city = new Game(grown, LongPlaytest.founding());
        city.playOnLiterals();
        city.getForeignAccounts().pinRate(city.getForeignAccounts().getRate());
        OrderSearchCheck.playtestCity(city, months, quiet);
        real.println("ref month " + city.getMonth() + " population " + city.getPopulationManager().getPopulation()
                + " capacity " + city.getHouseholdCapacity() + " homes " + city.getBuildingManager().getTotalHomes()
                + " cash " + city.getCash());
        System.exit(0);
    }
}
