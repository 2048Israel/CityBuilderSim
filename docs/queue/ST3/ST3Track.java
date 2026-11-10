package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

/**
 * ST3's tracker (2026-11-09): ScaleCheck section 9 at one month of section 6's town (ScaleCheck.ownTown()), its two copies
 * (K = 1 and TRACK_TIMES) played -Dst3.months months each, every sector and the branches held, and each household and city
 * figure below printed as the copy's miss against K times the city's, a month a line: which figure parts first.
 *
 * Usage: ST3Track <month>
 */
public class ST3Track {

    record Fig(String name, ToDoubleFunction<Game> read) { }

    static final Fig[] FIGS = {
        new Fig("people", g -> g.getPopulationManager().getPopulation()),
        new Fig("cash", Game::getCash),
        new Fig("savings", Game::getHouseholdDeposits),
        new Fig("deposits", g -> g.getBank().getDeposits()),
        new Fig("sectorDeposits", Game::getSectorDeposits),
        new Fig("gdp", g -> g.getEconomyManager().getMonthGdp()),
        new Fig("paper", g -> g.getHouseholdBalance().totalPaper()),
        new Fig("bonds", g -> g.getHouseholdBalance().totalBonds()),
        new Fig("shares0", g -> g.getHouseholdBalance().sharesHeld(0)),
        new Fig("cars", g -> g.getHouseholdBalance().totalCars()),
        new Fig("abroadUsd", g -> g.getHouseholdBalance().totalAbroadUsd()),
        new Fig("sentAbroad", g -> g.getHouseholdBalance().getSentAbroad()),
        new Fig("broughtHome", g -> g.getHouseholdBalance().getBroughtHome()),
        new Fig("mealSpend", g -> g.getHouseholdBalance().getMealSpend()),
        new Fig("luxurySpend", g -> g.getHouseholdBalance().getLuxurySpend()),
        new Fig("usedCarSpend", g -> g.getHouseholdBalance().getUsedCarSpend()),
        new Fig("carsBought", g -> g.getHouseholdBalance().getCarsBought()),
        new Fig("foodAid", g -> g.getHouseholdBalance().getFoodAssistancePaid()),
        new Fig("accountFees", g -> g.getHouseholdBalance().totalAccountFees()),
        new Fig("loanFees", g -> g.getHouseholdBalance().totalLoanFees()),
        new Fig("jobs", g -> g.getPopulationManager().getTotalJobs()),
        new Fig("households", g -> g.getSectors().realEstate().getHouseholdCount()),
    };

    public static void main(String[] a) throws Exception {
        Locale.setDefault(Locale.CANADA);
        int month = Integer.parseInt(a[0]), months = Integer.getInteger("st3.months", 2);
        PrintStream real = System.out, quiet = new PrintStream(OutputStream.nullOutputStream());
        ScaleCheck.out = quiet;
        ScaleCheck.quiet = quiet;
        Path root = TempDirs.create("st3track");
        GameFiles grown = new GameFiles(root.resolve("grown"), root.resolve("grown-no-legacy"));
        Game city = new Game(grown, LongPlaytest.founding());
        city.holdCausesOfDeathOff();
        city.playOnLiterals();
        city.getForeignAccounts().pinRate(city.getForeignAccounts().getRate());
        ScaleCheck.ownTown(city, month);
        ScaleCheck.quietly(() -> city.saveGame(10, "st3track"));
        GameFiles sites = ScaleCheck.emptySites(root, grown);
        long[] times = { 1, ScaleCheck.TRACK_TIMES };
        double[][][] seen = new double[2][months + 1][FIGS.length];
        for (int c = 0; c < 2; c++) {
            GameFiles files = new GameFiles(root.resolve("track" + times[c]), root.resolve("track" + times[c] + "-no-legacy"));
            ScaleCheck.scale(sites.saveFile(10), files.saveFile(10), times[c]);
            Files.copy(sites.historyFile(10), files.historyFile(10));
            Game g = new Game(files);
            g.playOnLiterals();
            g.holdCausesOfDeathOff();
            ScaleCheck.quietly(() -> g.loadGameSave(10));
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            g.getBusinessInvestment().holdBranches();
            for (int i = 0; i <= months; i++) {
                if (i > 0) ScaleCheck.quietly(() -> g.simulateMonths(1));
                for (int f = 0; f < FIGS.length; f++) seen[c][i][f] = FIGS[f].read().applyAsDouble(g);
            }
        }
        real.printf("month %d of the town; the copy's miss against %d x the city's (+ over, - under), and the city's figure%n", month, times[1]);
        for (int f = 0; f < FIGS.length; f++) {
            StringBuilder line = new StringBuilder(String.format("%-15s", FIGS[f].name()));
            for (int i = 0; i <= months; i++) {
                double city1 = seen[0][i][f], copy = seen[1][i][f];
                double miss = (copy - times[1] * city1) / Math.max(1e-9, Math.abs(times[1] * city1));
                line.append(String.format("  m%d %+.2e (%.4g)", i, miss, city1));
            }
            real.println(line);
        }
        System.exit(0);
    }
}
