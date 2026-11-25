package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verifies the graph history: recording, alignment, and the round trip. Not
 * part of the game.
 *
 * WHY THIS EXISTS
 *
 * The history went from eight series to twenty-three, and every one of the
 * three ways that can go wrong is silent.
 *
 * 1. A SERIES THAT IS NEVER RECORDED looks exactly like one that is, until you
 *    open the graph and it is empty - which is a long way from where the
 *    mistake was made.
 * 2. A SERIES MISSING FROM restoreFrom() records perfectly all session and
 *    vanishes on reload. The live game and the reloaded game disagree and
 *    nothing says so.
 * 3. A SHORT SERIES - one added after a city was already being played - lines
 *    up with the END of the month axis and not the start. Draw it from the left
 *    instead and last decade's sickness appears in the founding years, plotted
 *    confidently against the wrong months.
 *
 * The third is the interesting one, because it is the only bug here that
 * produces a graph that looks completely fine.
 *
 * And since 0.7.55, a fourth (section 6): past HistorySave.MONTHLY_KEPT months
 * the oldest years fold a year to an entry, each series by its rule, and
 * every reader has to read a folded year as a year - a fold that summed a
 * level, or a chart that drew a year's flow as one month's, looks fine too.
 *
 * And since CU2 (REV-CU2-1), a fifth (section 7): the eight prices roundSig
 * records are written at today's prices to six figures and read back times
 * FACTOR, and a reload that moved one bit of one of them would move the bank's
 * volatility, and every trace played from the file, without a word.
 *
 * FS17: sections 1, 2, 2b and 2c are identities over whatever was played and stay so. What sections 2d, 2e and 2f read off a played city
 * is caused now: the bank's fees, loss record and sheet on one fixture bank (a founding with a loan written to Retail, its capital record
 * put by hand to no bad year and then to the worst one, its fee taken as the settle takes it - the "lent" city, scratch files), and the
 * schools' diplomas at the month they first flow, inside a bound that fails as a fixture.
 */
public class HistoryCheck {

    static int fails = 0;

    /** The game narrates every month to stdout; the findings are the output here. */
    static final PrintStream OUT = System.out;
    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
    });

    /** Runs a stretch of months without the monthly report burying the results. */
    static void quietly(Runnable work) {
        System.setOut(QUIET);
        try { work.run(); } finally { System.setOut(OUT); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double a, double b) {
        boolean ok = Math.abs(a - b) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label,
                ok ? "OK" : String.format("FAIL  %.6f != %.6f", a, b));
    }

    public static void main(String[] args) throws Exception {

        System.out.printf("HistoryCheck native PID=%d runtime=%s home=%s earth=%s world=%s%n", ProcessHandle.current().pid(),
                System.getProperty("java.runtime.version"), System.getProperty("java.home"), System.getProperty("cbs.earth"), System.getProperty("cbs.world"));
        try (var bytes = HistoryCheck.class.getResourceAsStream("HistoryCheck.class")) {
            System.out.println("HistoryCheck loaded class SHA256=" + java.util.HexFormat.of().formatHex(
                    java.security.MessageDigest.getInstance("SHA-256").digest(bytes.readAllBytes())));
        }

        Path root = TempDirs.create("history");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ============ 1. every series is recorded, every month ============ */
        System.out.println("--- every series records, every month ---");

        Game city = new Game(files);
        int months = 30;
        quietly(() -> { city.newGame(); city.simulateMonths(months); });

        HistorySave h = city.getHistorySave();
        System.out.println("  months on the axis: " + h.months());

        assertTrue("the axis has a point per month lived", h.months() >= months);

        /*
         * EVERY series, by walking the map rather than naming them.
         *
         * Naming them here would be a second list to keep in step with
         * HistorySave's, and a series left off BOTH lists is a series nobody
         * checks - which is exactly the failure mode. Walking seriesByName()
         * means adding a series to the recorder automatically adds it here.
         */
        Map<String, List<? extends Number>> series = h.seriesByName();
        Map<String, Double> power = PowerView.series(PowerView.of(city));
        assertTrue("the thirty-month fixture has native power observations", !power.isEmpty());
        System.out.println("  series kept: " + series.size());
        assertTrue("there are more than the original eight", series.size() > 8);

        int market = 0;
        for (Map.Entry<String, List<? extends Number>> e : series.entrySet()) {
            boolean shares = e.getKey().startsWith("sharePrice:") || e.getKey().startsWith("shareValue:");
            if (shares) {
                // A company's price runs from the month it LISTED to the end
                // of the axis: never longer, never a month short at the end.
                market++;
                assertTrue("  " + e.getKey() + " runs from its listing to the end",
                        e.getValue().size() >= 1 && e.getValue().size() <= h.months());
                continue;
            }
            // Only exact keys emitted by this native projection qualify; an unknown power name fails.
            if (power.containsKey(e.getKey())) {
                powerSuffix(h, e.getKey(), power.get(e.getKey()));
                continue;
            }
            if (e.getKey().startsWith("power")) assertTrue("  " + e.getKey() + " is an emitted native power key", false);
            assertTrue("  " + e.getKey() + " has a value for every month",
                    e.getValue().size() == h.months());
        }
        assertTrue("the companies' share prices are among them", market > 0);
        double[] bankPrice = h.aligned(HistorySave.priceKey(Equity.COMPANIES[Equity.BANK]));
        boolean priced = bankPrice.length > 0 && !Double.isNaN(bankPrice[bankPrice.length - 1])
                && bankPrice[bankPrice.length - 1] > 0;
        assertTrue("  the bank, listed at the founding, has a price in the last month", priced);

        /*
         * AND THEY ARE NOT ALL ZERO.
         *
         * Length is not recording. A series wired to a getter that returns 0 -
         * or to the wrong getter on an object that has not been updated yet -
         * has the right length and no content, and looks identical on a graph
         * to a city where nothing happened. A thirty-month city with people in
         * it has a population, a wage bill and a food price.
         */
        for (String key : new String[]{"population", "gdp", "totalWage", "foodPrice",
                                       "materialsPrice", "landPrice", "revenue"}) {
            double[] v = h.aligned(key);
            boolean anyNonZero = false;
            for (double d : v) if (!Double.isNaN(d) && d != 0) anyNonZero = true;
            assertTrue("  " + key + " actually carries values", anyNonZero);
        }

        /* ============ 2. it survives a save and a reload ============ */
        System.out.println("\n--- and it survives a reload ---");

        assertTrue("the city saved", city.saveGame(1, "history").ok);

        Game reloaded = new Game(files);
        quietly(() -> reloaded.loadGameSave(1));
        HistorySave back = reloaded.getHistorySave();

        assertTrue("the axis came back whole", back.months() == h.months());

        /*
         * Compared series by series off the map, so a line forgotten in
         * restoreFrom() fails here by name instead of silently emptying a
         * graph. This is the assertion that made the eight hand-written
         * assignments in Game unnecessary.
         */
        int checked = 0;
        for (String key : series.keySet()) {
            double[] before = h.aligned(key);
            double[] after = back.aligned(key);
            boolean same = before.length == after.length;
            for (int i = 0; same && i < before.length; i++) {
                // A month nobody was counting is NaN on both sides, and NaN
                // is not within anything of NaN: a company's share price
                // starts the month it lists, and the months before are that.
                same = (Double.isNaN(before[i]) && Double.isNaN(after[i]))
                        || Math.abs(before[i] - after[i]) < 1e-9;
            }
            if (!same) {
                System.out.println("  MISMATCH in " + key
                        + " - is it missing from HistorySave.restoreFrom()?");
            }
            assertTrue("  " + key + " reloaded identically", same);
            checked++;
        }
        System.out.println("  " + checked + " series compared across the reload");

        /* ============ 2b. every sector's two series (0.7.4) ============

           The sector list's sparkline and its head count: netIncome:<sector>,
           the month's net income after tax off the sector's own statement,
           and workers:<sector>, its posts filled. Recorded beside the rest,
           so the net income is the figure SectorBooks files for the same
           month - the one the list's card prints - and the workers the
           sector's own Sector.getWorkers().
           ================================================================= */
        System.out.println("\n--- every sector's net income and workers, every month ---");

        SectorBooks books = city.getSectorBooks();
        int sectors = 0;
        boolean anyEarned = false, anyEmployed = false;
        for (Sector s : city.getSectors().all()) {
            List<? extends Number> income = h.seriesByName().get(HistorySave.netIncomeKey(s.key()));
            List<? extends Number> staff  = h.seriesByName().get(HistorySave.workersKey(s.key()));
            boolean whole = income != null && staff != null
                    && income.size() == h.months() && staff.size() == h.months();
            assertTrue("  " + s.key() + " has both, a value for every month", whole);
            if (!whole) continue;
            sectors++;
            double lastIncome = income.get(income.size() - 1).doubleValue();
            double lastStaff = staff.get(staff.size() - 1).doubleValue();
            // Kept to the cent of a thousand, as every money series is: the
            // money read to the half cent, or past ten billion units a part
            // in a trillion of it (MoneyAudit.tolerance(), 0.7.54).
            assertTrue("  ...its last month's net income is SectorBooks' for that month",
                    Math.abs(lastIncome - books.get(s).netIncome())
                            <= MoneyAudit.tolerance(.005 + 1e-9, books.get(s).netIncome()));
            assertTrue("  ...and its last month's workers are the sector's posts filled",
                    Math.abs(lastStaff - s.getWorkers()) <= .005 + 1e-9);
            if (lastIncome != 0) anyEarned = true;
            if (lastStaff > 0) anyEmployed = true;
            double[] incomeBack = back.aligned(HistorySave.netIncomeKey(s.key()));
            double[] staffBack = back.aligned(HistorySave.workersKey(s.key()));
            assertTrue("  ...and both came back from the save",
                    incomeBack.length == h.months() && staffBack.length == h.months()
                    && Math.abs(incomeBack[incomeBack.length - 1] - lastIncome) < 1e-9
                    && Math.abs(staffBack[staffBack.length - 1] - lastStaff) < 1e-9);
        }
        assertTrue("every sector in the city has its two series", sectors == city.getSectors().size());
        assertTrue("...and some sector earned or lost something, so the income is read", anyEarned);
        assertTrue("...and some sector employs somebody, so the workers are read", anyEmployed);

        /* ============ 2c. GDP's four parts (0.7.6) ============

           The Reports page draws real GDP in layers - consumption,
           investment and government stacked, the GDP line over them, the gap
           net exports - off four series kept beside gdp. A part wired to the
           wrong getter would still stack into a plausible mountain; the
           identity is what says it is the right one. Each part is rounded to
           the history's cent on its own, so the four can miss the rounded
           sum by four half-cents and the sum's own half - and no more.
           ================================================================= */
        System.out.println("\n--- GDP's four parts, every month, adding up ---");

        NationalAccounts na = city.getEconomyManager().getNationalAccounts();
        assertTrue("the city's own accounts: C+I+G+NX is this month's GDP",
                Math.abs(na.getConsumption() + na.getInvestment() + na.getGovernment()
                        + na.getNetExports() - na.getGdp())
                        <= 1e-9 * Math.max(1, Math.abs(na.getGdp())));
        for (String part : YearBook.GDP_PARTS) {
            List<? extends Number> kept = h.seriesByName().get(part);
            assertTrue("  " + part + " is kept, a value for every month",
                    kept != null && kept.size() == h.months());
        }
        double[] gdpKept = h.aligned("gdp");
        double[][] parts = new double[YearBook.GDP_PARTS.length][];
        for (int p = 0; p < parts.length; p++) parts[p] = h.aligned(YearBook.GDP_PARTS[p]);
        double worstMiss = 0;
        boolean anyConsumption = false;
        for (int i = 0; i < h.months(); i++) {
            double sum = 0;
            for (double[] part : parts) sum += part[i];
            worstMiss = Math.max(worstMiss, Math.abs(sum - gdpKept[i]));
            if (parts[0][i] > 0) anyConsumption = true;
        }
        System.out.printf("  the parts miss the kept GDP by at most %.4f (thousands)%n", worstMiss);
        assertTrue("C+I+G+NX is the month's kept GDP to the cent, every month", worstMiss <= .025 + 1e-9);
        assertTrue("...and the households bought something, so C is read", anyConsumption);
        int last = h.months() - 1;
        assertTrue("the last month's parts are the accounts' own, to the cent",
                Math.abs(parts[0][last] - na.getConsumption()) <= .005 + 1e-9
                        && Math.abs(parts[1][last] - na.getInvestment()) <= .005 + 1e-9
                        && Math.abs(parts[2][last] - na.getGovernment()) <= .005 + 1e-9
                        && Math.abs(parts[3][last] - na.getNetExports()) <= .005 + 1e-9);
        double[] yearOfGdp = YearBook.realGdpYear(h);
        double yearOfParts = 0;
        for (String part : YearBook.GDP_PARTS) yearOfParts += YearBook.realYear(h, part)[last];
        double index = h.aligned("priceIndex")[last];
        System.out.printf("  a year of real GDP %.2f, of its four parts %.2f%n", yearOfGdp[last], yearOfParts);
        assertTrue("a rolling year of the four in founding money is the year of real GDP",
                !Double.isNaN(yearOfGdp[last])
                        && Math.abs(yearOfParts - yearOfGdp[last]) <= 12 * .025 / index + 1e-9);
        assertTrue("...and no year before twelve months of them",
                Double.isNaN(YearBook.realYear(h, "consumption")[YearBook.MONTHS_A_YEAR - 2]));

        /* ============ 2d. the dial and the bank's three prices (0.7.7) ============

           policyRate, bankPrime, bankDepositRate and bankFees replaced the
           strain premium's series when the bank began pricing by cost. Section
           1 already proves each is recorded every month and section 2 that it
           reloads; this is that each is wired to the figure it names - the
           last month against the model's own getters, to the history's
           rounding - and that the founding bank charged a fee, so the fee
           series is read and not a column of zeros.
           ================================================================= */
        System.out.println("\n--- the dial and the bank's three prices ---");

        Bank lender = city.getBank();
        double dial = city.getDebtManager().getPolicyRate();
        System.out.printf("  last month: dial %.4f, prime %.4f, savers %.4f, fees %.2f%n",
                h.aligned("policyRate")[last], h.aligned("bankPrime")[last],
                h.aligned("bankDepositRate")[last], h.aligned("bankFees")[last]);
        assertTrue("the last month's policyRate is the dial",
                Math.abs(h.aligned("policyRate")[last] - dial) <= 5e-5 + 1e-12);
        assertTrue("...its bankPrime is Bank.prime() at that dial",
                Math.abs(h.aligned("bankPrime")[last] - lender.prime(dial)) <= 5e-5 + 1e-12);
        assertTrue("...its bankDepositRate is what savers were paid",
                Math.abs(h.aligned("bankDepositRate")[last] - lender.depositRate()) <= 5e-5 + 1e-12);
        assertTrue("...and its bankFees is the month's fee income, to the cent",
                Math.abs(h.aligned("bankFees")[last] - lender.feeIncome())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lender.feeIncome()));
        double[] dials = h.aligned("policyRate"), primes = h.aligned("bankPrime");
        boolean primeOverDial = true;
        for (int i = 0; i < h.months(); i++) {
            // Every part of the price is at least zero and the first is the
            // dial itself, so prime is never under it (rounding allowed).
            if (primes[i] < Math.max(0, dials[i]) - 1e-4) primeOverDial = false;
        }
        assertTrue("prime is never under the dial, in any month", primeOverDial);
        /*
         * FS17: the fee series used to be read off the played city - "the founding bank charged a fee in some month" held only while an
         * unbuilt default city's thirty months happened to charge one - and the bank's capital (2e) off the same city's bank, whether it
         * had lived through a bad year and had anything on its sheet. Both are CAUSED on one fixture city now, a founding played a month, the
         * bank lent by hand through its own call (Bank.lendCarry(): cash out, book up, the claim as large as its equity so its weighted
         * book is a figure against its capital), its loss record put to a bank that has lived no year by Bank.restoreCapitalRecord(), the fee
         * on the loan taken by Bank.takeLoanFees() as Game's settle takes it (a business loan written between months is not settled: the
         * month's audit starts after it, and a founding's Retail cannot carry one), and the month recorded by the game's own recordMonth().
         * 2d reads the fee, 2e the six series and the leverage pair, on it.
         */
        Game lent = new Game(GameFiles.scratch("historycheck-bank"));
        quietly(() -> { lent.newGame(); lent.simulateMonths(1); });
        Bank lentBank = lent.getBank();
        double face = lentBank.equity();
        lentBank.lendCarry(face);
        lentBank.restoreCapitalRecord(new double[3 + 4 * Bank.YEAR_MONTHS]);
        lentBank.takeLoanFees(BusinessDebtManager.feeOn(face));
        quietly(lent::recordMonth);
        HistorySave lentHist = lent.getHistorySave();
        int lentLast = lentHist.months() - 1;
        assertTrue("fixture: the bank lent, and took the fee on the loan this month",
                BusinessDebtManager.feeOn(face) > 0 && lentBank.feeIncome() >= BusinessDebtManager.feeOn(face));
        assertTrue("...and the bank's bankFees reads that month's fee income, no less than the fee on its loan",
                Math.abs(lentHist.aligned("bankFees")[lentLast] - lentBank.feeIncome())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lentBank.feeIncome())
                        && lentHist.aligned("bankFees")[lentLast] >= BusinessDebtManager.feeOn(face) - (.005 + 1e-9));

        /* ============ 2e. the bank's capital (0.7.8) ============

           Six series for what the bank holds and what it does with it: its
           capital ratio, the target it chose, its allowance, the month's
           provision, its dividend and its return on equity. Wired to the
           figures they name, the last month against the model's own getters
           to the history's rounding - and the founding bank's target, which
           has no bad year behind it, is the minimum and the conservation
           buffer in every month it is recorded.
           ================================================================= */
        System.out.println("\n--- the bank's capital ---");

        // FS17: the six series, the leverage pair and the targets below are read on the caused fixture bank above (lent), whose loss record
        // and sheet are set, not on the played city's bank.
        System.out.printf("  last month: ratio %.4f, target %.4f, allowance %.2f, provisions %.2f, dividends %.2f, ROE %.4f%n",
                lentHist.aligned("bankCapitalRatio")[lentLast], lentHist.aligned("bankCapitalTarget")[lentLast],
                lentHist.aligned("bankAllowance")[lentLast], lentHist.aligned("bankProvisions")[lentLast],
                lentHist.aligned("bankDividends")[lentLast], lentHist.aligned("bankReturnOnEquity")[lentLast]);
        double weighted = lentBank.getWeightedBook();
        assertTrue("the last month's bankCapitalRatio is Bank.capitalRatio(), clamped at ten",
                Math.abs(lentHist.aligned("bankCapitalRatio")[lentLast]
                        - (weighted > 0 ? Math.min(10, lentBank.capitalRatio()) : 10)) <= 5e-5 + 1e-12);
        assertTrue("...its bankCapitalTarget is the target the bank chose",
                Math.abs(lentHist.aligned("bankCapitalTarget")[lentLast] - lentBank.capitalTarget()) <= 5e-5 + 1e-12);
        assertTrue("...its bankAllowance is what it has set aside, to the cent",
                Math.abs(lentHist.aligned("bankAllowance")[lentLast] - lentBank.getAllowance())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lentBank.getAllowance()));
        assertTrue("...its bankProvisions is the month's provision, to the cent",
                Math.abs(lentHist.aligned("bankProvisions")[lentLast] - lentBank.provisions())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lentBank.provisions()));
        assertTrue("...its bankDividends is what it paid its owners, to the cent",
                Math.abs(lentHist.aligned("bankDividends")[lentLast] - lentBank.getDividendsPaid())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lentBank.getDividendsPaid()));
        assertTrue("...and its bankReturnOnEquity is the month's return, a year, clamped at ten",
                Math.abs(lentHist.aligned("bankReturnOnEquity")[lentLast]
                        - Math.max(-10, Math.min(10, lentBank.returnOnEquity()))) <= 5e-5 + 1e-12);
        double[] targets = lentHist.aligned("bankCapitalTarget");
        boolean young = true;
        for (int i = 0; i < lentHist.months(); i++) {
            if (!Double.isNaN(targets[i])
                    && Math.abs(targets[i] - (Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER)) > 5e-5 + 1e-12) young = false;
        }
        assertTrue("fixture: the founding bank has lived through no bad year", lentBank.getWorstLossRate() <= Bank.CONSERVATION_BUFFER);
        assertTrue("...so it targets the minimum and the conservation buffer in every month", young);
        // FS17: ...and the other side of it, the same bank with a bad year in its record (Bank.restoreCapitalRecord(), the worst year it has
        // lived at the buffer's ceiling): the series reads the target it chose, above the minimum and the conservation buffer.
        double[] badYear = new double[3 + 4 * Bank.YEAR_MONTHS];
        badYear[1] = Bank.MAX_BUFFER;
        lentBank.restoreCapitalRecord(badYear);
        quietly(lent::recordMonth);
        lentHist = lent.getHistorySave();
        lentLast = lentHist.months() - 1;
        assertTrue("a bank that has lived through a bad year targets the minimum and the buffer that year sized, in the month it is recorded",
                lentBank.capitalTarget() == Bank.CAPITAL_RATIO + Bank.MAX_BUFFER
                        && Math.abs(lentHist.aligned("bankCapitalTarget")[lentLast] - lentBank.capitalTarget()) <= 5e-5 + 1e-12
                        && lentHist.aligned("bankCapitalTarget")[lentLast] > Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER + 5e-5);
        /*
         * ...AND THE OTHER MEASURE (A7, 0.7.46): the leverage ratio - which
         * can bind where the clamped capital ratio reads ten - and the target
         * the bank holds on it, every month, at the getters' own figures.
         */
        assertTrue("fixture: the bank has something on its sheet, so its leverage ratio is a figure",
                lentBank.exposure() > 0 && lentBank.leverageRatio() < 10);
        // ...and the outbreak the year book names an epidemic on (A8, 0.7.46), as Health reads it.
        assertTrue("the outbreak series is Health's outbreak, 0 between them, every month",
                h.monthsRecorded("outbreak") == h.months()
                        && Math.abs(h.aligned("outbreak")[last] - city.getHealth().getOutbreakSeverity()) <= 5e-5 + 1e-12);
        assertTrue("the leverage ratio and its target are recorded every month as the bank reads them",
                lentHist.monthsRecorded("bankLeverageRatio") == lentHist.months() && lentHist.monthsRecorded("bankLeverageTarget") == lentHist.months()
                        && Math.abs(lentHist.aligned("bankLeverageRatio")[lentLast] - Math.min(10, lentBank.leverageRatio())) <= 5e-5 + 1e-12
                        && Math.abs(lentHist.aligned("bankLeverageTarget")[lentLast] - lentBank.leverageTarget()) <= 5e-5 + 1e-12);

        /* ============ 2f. the month's graduates (A6, 0.7.46) ============

           The series recorded the movement between education bands, whose
           +1s and -1s net to nothing, so it read zero in every city. It is
           the month's gains now (Education.gainedThisMonth()), which can never
           be negative. CAUSED: a funded town of EducationCheck's with every
           basic stage built, whose high schools hand out diplomas within a
           few months.
           ================================================================= */
        System.out.println("\n--- the month's graduates are the people who gained a qualification ---");
        /*
         * FS17: this played twelve months and read the twelfth for a month the high schools handed out diplomas in. It plays a month at a
         * time until they do, and reads THAT month: the diplomas the schools hand out are Education.getNewDiplomas() (the teens ageing out
         * at the ladder's coverage, once the schools are built and staffed, which the model gives no month for), and the twelve months are
         * the fixture's bound, not a reading - a town whose schools hand out none by then fails the fixture line below, not the series.
         * The town is then played on to twelve months in all, as 2g reads a company a household holds a share of in it.
         */
        final int schoolBound = 12;
        final int[] schoolMonths = { 0 };
        final double[] atFirst = new double[3];   // the month the diplomas first flowed: the schools' diplomas, what they gained, the series' point
        Game[] schooledBox = new Game[1];
        quietly(() -> {
            schooledBox[0] = EducationCheck.city(null);
            EducationCheck.build(schooledBox[0], "Elementary School", 2);
            EducationCheck.build(schooledBox[0], "Middle School", 2);
            EducationCheck.build(schooledBox[0], "High School", 2);
            while (!(schooledBox[0].getEducation().getNewDiplomas() > 0) && schoolMonths[0] < schoolBound) {
                schooledBox[0].simulateMonths(1);
                schoolMonths[0]++;
            }
            double[] pointsThen = schooledBox[0].getHistorySave().aligned("graduates");
            atFirst[0] = schooledBox[0].getEducation().getNewDiplomas();
            atFirst[1] = schooledBox[0].getEducation().gainedThisMonth();
            atFirst[2] = pointsThen[pointsThen.length - 1];
            for (; schoolMonths[0] < schoolBound; schoolMonths[0]++) schooledBox[0].simulateMonths(1);
        });
        System.out.printf("  the month the diplomas first flowed: %.3f handed out, %.3f gained, %.2f recorded%n", atFirst[0], atFirst[1], atFirst[2]);
        Game schooled = schooledBox[0];
        double[] graduated = schooled.getHistorySave().aligned("graduates");
        boolean neverNegative = true;
        for (double v : graduated) if (v < 0) neverNegative = false;
        assertTrue("fixture: the town's high schools handed out diplomas this month", atFirst[0] > 0);
        assertTrue("the graduates series is never negative", neverNegative);
        assertTrue("...and its last month is what the schools gained, to the history's rounding",
                Math.abs(atFirst[2] - atFirst[1]) <= .005 + 1e-9 && atFirst[2] > 0);

        /* ============ 2g. a consolidated company's price (C5, 0.7.48) ============

           A company's price is recorded per founding share, continuous through
           every split, and a consolidated company's is far under a
           ten-thousandth - which four decimal places recorded as 0, a share
           price of nothing, and its volatility (the warrants' value) read off
           those noughts. CAUSED, in the schooled town above (its history is
           read by nothing after): one share sold to the world at about a
           millionth of the founding price - a figure of ten digits - and the
           step's consolidation run on it; then the month recorded.
           ================================================================= */
        System.out.println("\n--- a consolidated company's price is recorded, not rounded to nothing ---");
        Exchange cx = schooled.getExchange();
        Equity cr = schooled.getEquity();
        int consolidated = -1;
        Household seller = null;
        for (int c = 0; c < Equity.COMPANIES.length && consolidated < 0; c++) {
            if (!(cr.getShares(c) > 0) || !(cx.fair(c) > Exchange.MIN_FAIR)) continue;
            for (Household cell : schooled.getHouseholdBalance().cells()) {
                if (cell.households() > 0 && cell.shares[c] * cell.households() >= 1) { seller = cell; consolidated = c; break; }
            }
        }
        assertTrue("fixture: a listed company a household holds a share of", consolidated >= 0);
        double tiny = 1.234567891e-6 * cr.foundingPrice();
        cx.bookOf(consolidated).withdrawAll();
        cx.tradeForCheck(consolidated, Exchange.CELL + seller.key(), OrderBook.Side.SELL, tiny, 1);
        cx.tradeForCheck(consolidated, Exchange.WORLD, OrderBook.Side.BUY, tiny, 1);
        cx.splitForCheck();
        double perFounding = cx.pricePerFoundingShare(consolidated);
        assertTrue("fixture: it consolidated, and four places would record its price as nothing",
                cx.getSplit(consolidated) < 1 && perFounding > 0 && Math.round(perFounding * 10000.0) == 0);
        schooled.getHistorySave().recordMonth(schooled);
        double[] pricesKept = schooled.getHistorySave().aligned(HistorySave.priceKey(Equity.COMPANIES[consolidated]));
        double kept = pricesKept[pricesKept.length - 1];
        System.out.printf("  %s: %.9g a founding share, recorded as %.9g%n", Equity.COMPANIES[consolidated], perFounding, kept);
        assertTrue("a consolidated company's price is recorded, not rounded to nothing",
                kept > 0 && Math.abs(kept - perFounding) <= .5e-5 * perFounding
                        && kept == HistorySave.roundSig(perFounding));

        /* ============ 3. A SHORT SERIES LINES UP WITH THE END ============

           The one that draws a convincing wrong graph.

           A city played before a series existed loads with that series empty
           and the axis full. Play on and the series fills from THAT MONTH
           FORWARD, so it describes the END of the axis. Padding it at the front
           is the only correct reading; padding it at the back - or, worse,
           plotting it from index zero - takes the last few months of data and
           draws them over the city's founding, and nothing about the result
           looks wrong.

           Simulated by hand rather than waited for, because the alternative is
           keeping a pre-sickness save file around forever as a fixture.
           ============================================================== */
        System.out.println("\n--- a series added late lines up with the END ---");

        Path historyFile = files.historyFile(1);
        String json = Files.readString(historyFile);

        // Strip one series out of the file entirely - exactly what a history
        // written before that series existed looks like.
        String stripped = dropSeries(json, "sickRate");
        assertTrue("the fixture actually removed the series",
                json.contains("\"sickRate\"") && !stripped.contains("\"sickRate\""));
        Files.writeString(historyFile, stripped);

        Game older = new Game(files);
        quietly(() -> older.loadGameSave(1));
        HistorySave old = older.getHistorySave();

        System.out.println("  months: " + old.months()
                + ", sickRate values: " + old.seriesByName().get("sickRate").size());

        assertTrue("the axis is untouched by the missing series",
                old.months() == h.months());
        assertTrue("...and the missing series is empty, not absent",
                old.seriesByName().get("sickRate") != null
                        && old.seriesByName().get("sickRate").isEmpty());

        double[] absent = old.aligned("sickRate");
        assertTrue("aligned() still returns a full-length array",
                absent.length == old.months());

        boolean allNaN = true;
        for (double d : absent) if (!Double.isNaN(d)) allNaN = false;
        /*
         * NaN AND NOT ZERO, which is the whole distinction. A sick rate of 0%
         * is a healthy city; "we were not counting" is not a claim about the
         * city's health at all, and a graph that draws the second as the first
         * invents a decade of perfect health.
         */
        assertTrue("...reading as NaN - not-recorded is not the same as zero", allNaN);

        // Now play on, and check the new months land at the RIGHT END.
        quietly(() -> older.simulateMonths(4));
        HistorySave grown = older.getHistorySave();

        int lived = grown.months();
        int recorded = grown.seriesByName().get("sickRate").size();
        System.out.println("  after 4 more months: " + lived
                + " on the axis, " + recorded + " sick rates");

        assertTrue("only the new months have the series", recorded == 4);

        double[] aligned = grown.aligned("sickRate");
        assertTrue("aligned() covers the whole axis", aligned.length == lived);

        for (int i = 0; i < lived - recorded; i++) {
            assertTrue("  month " + grown.getMonth().get(i) + " is blank, as it should be",
                    Double.isNaN(aligned[i]));
        }
        boolean tailIsReal = true;
        for (int i = lived - recorded; i < lived; i++) {
            if (Double.isNaN(aligned[i])) tailIsReal = false;
        }
        assertTrue("...and the LAST four months carry the data", tailIsReal);

        /* ============ 3a. a year of a flow (B7, 0.7.47) ============

           Government's every "of GDP" reads a line's last twelve recorded
           months once History has twelve of it (GovernmentScreen's
           TRAILING_MONTHS, which the model build cannot see), where it read
           the month x 12 and a busy month read as a year. So the twelve months
           recentTotal() adds must be the twelve the accounts struck, in
           order - and a series History began four months ago sums only its
           four, which is why the tab waits for twelve.
           ================================================================= */
        System.out.println("\n--- twelve recorded months of a flow are its trailing year ---");
        int year = 12;
        double[] sick = grown.aligned("sickRate");
        double four = 0;
        for (int i = lived - recorded; i < lived; i++) four += sick[i];
        assertTrue("a series recorded for four months sums its four, and has four, not a year",
                grown.monthsRecorded("sickRate") == 4 && grown.recentTotal("sickRate", year) == four);
        double[] struck = new double[year];
        for (int m = 0; m < year; m++) {
            quietly(() -> older.simulateMonths(1));
            double v = older.getEconomyManager().getNationalAccounts().getTotalRevenue();
            struck[m] = Math.round(v * 100.0) / 100.0;   // HistorySave's round2, which is private
        }
        double struckYear = 0;
        for (double v : struck) struckYear += v;
        HistorySave yearOn = older.getHistorySave();
        assertTrue("fixture: a year of revenue, recorded", struckYear > 0 && yearOn.monthsRecorded("revenue") >= year);
        assertTrue("twelve recorded months of a flow are its trailing year", yearOn.recentTotal("revenue", year) == struckYear);

        /* ============ 3b. a 0.7.6 history still loads (0.7.7) ============

           A history written before 0.7.7 carries "bankPremium", a series
           HistorySave no longer has, and none of the four that replaced it.
           Gson skips a key the class does not declare; this is the proof
           that it does here, and that the four new series then fill from the
           end like any series added late.
           ================================================================= */
        System.out.println("\n--- a history from before the premium went ---");

        String before077 = json;
        // ...and without 0.7.8's six, which a history from before 0.7.7 has
        // none of either (they fill the same way - asserted at the end).
        // ...nor 0.7.35's parity beside the rate, nor XR1's fair level and market swing.
        for (String added : new String[]{"policyRate", "bankPrime", "bankDepositRate", "bankFees",
                "bankCapitalRatio", "bankCapitalTarget", "bankAllowance", "bankProvisions",
                "bankDividends", "bankReturnOnEquity", "fxParity", "fxFair", "fxMarket", "usPrices"}) {
            before077 = dropSeries(before077, added);
        }
        StringBuilder premiums = new StringBuilder("\"bankPremium\":[");
        for (int i = 0; i < h.months(); i++) premiums.append(i == 0 ? "" : ",").append("0.18");
        premiums.append("],");
        before077 = before077.replaceFirst("\\{", "{" + premiums);
        assertTrue("the fixture has the old series and none of the four new ones",
                before077.contains("\"bankPremium\"") && !before077.contains("\"bankPrime\"")
                        && !before077.contains("\"policyRate\""));
        Files.writeString(historyFile, before077);

        Game old076 = new Game(files);
        quietly(() -> old076.loadGameSave(1));
        HistorySave was = old076.getHistorySave();
        assertTrue("the old history loads with its axis whole", was.months() == h.months());
        assertTrue("...the premium it carried is not a series any more",
                !was.seriesByName().containsKey("bankPremium"));
        assertTrue("...and the bank's prices are empty, not absent",
                was.seriesByName().get("bankPrime") != null
                        && was.seriesByName().get("bankPrime").isEmpty());
        assertTrue("...and so is LB3's price level of the game dollar (usPrices), empty and not absent",
                was.seriesByName().get("usPrices") != null && was.seriesByName().get("usPrices").isEmpty());
        quietly(() -> old076.simulateMonths(2));
        assertTrue("...and usPrices fills from the end as the others do, at the world's own level, drawn as not recorded before",
                old076.getHistorySave().seriesByName().get("usPrices").size() == 2
                        && Double.isNaN(old076.getHistorySave().aligned("usPrices")[0])
                        && old076.getHistorySave().aligned("usPrices")[old076.getHistorySave().months() - 1] == old076.getWorldEconomy().getPriceLevel());
        assertTrue("...and fill from the end once the city plays on",
                old076.getHistorySave().seriesByName().get("bankPrime").size() == 2
                        && !Double.isNaN(old076.getHistorySave().aligned("bankPrime")
                                [old076.getHistorySave().months() - 1]));
        assertTrue("...and so do the bank's capital series (0.7.8), empty until then",
                was.seriesByName().get("bankCapitalTarget") != null
                        && old076.getHistorySave().seriesByName().get("bankCapitalTarget").size() == 2
                        && old076.getHistorySave().seriesByName().get("bankAllowance").size() == 2
                        && !Double.isNaN(old076.getHistorySave().aligned("bankCapitalTarget")
                                [old076.getHistorySave().months() - 1]));
        assertTrue("...and so does parity beside the rate (0.7.35), drawn as not recorded before",
                was.seriesByName().get("fxParity") != null
                        && old076.getHistorySave().seriesByName().get("fxParity").size() == 2
                        && Double.isNaN(old076.getHistorySave().aligned("fxParity")[0])
                        && !Double.isNaN(old076.getHistorySave().aligned("fxParity")
                                [old076.getHistorySave().months() - 1]));
        assertTrue("...and so do XR1's fair level and market swing, drawn as not recorded before",
                was.seriesByName().get("fxFair") != null && was.seriesByName().get("fxMarket") != null
                        && old076.getHistorySave().seriesByName().get("fxFair").size() == 2
                        && old076.getHistorySave().seriesByName().get("fxMarket").size() == 2
                        && Double.isNaN(old076.getHistorySave().aligned("fxFair")[0])
                        && !Double.isNaN(old076.getHistorySave().aligned("fxFair")
                                [old076.getHistorySave().months() - 1])
                        && !Double.isNaN(old076.getHistorySave().aligned("fxMarket")
                                [old076.getHistorySave().months() - 1]));

        /* ============ 4. the derived series do not divide by zero ============

           Unemployment, GDP per capita and the average wage are all a ratio
           over something that is ZERO in a brand new city - no workforce, no
           population. Deriving them on the way to the screen is right, and it
           puts a division one line away from a graph.
           ================================================================= */
        System.out.println("\n--- derived series on a city with nobody in it ---");

        Game empty = new Game(files);
        quietly(() -> { empty.newGame(); empty.simulateMonths(2); });
        HistorySave e = empty.getHistorySave();

        double[] pop = e.aligned("population");
        double[] work = e.aligned("workforce");
        System.out.printf("  month 1: population %.0f, workforce %.0f%n", pop[0], work[0]);

        for (int i = 0; i < e.months(); i++) {
            double w = work[i], j = e.aligned("jobs")[i];
            double unemployment = w > 0 ? Math.max(0, (w - j) / w) : Double.NaN;
            assertTrue("  month " + i + ": unemployment is a number or nothing, never infinite",
                    Double.isNaN(unemployment) || !Double.isInfinite(unemployment));
        }

        /* ============ 5. a new game forgets it ============ */
        System.out.println("\n--- and a new game starts with a blank sheet ---");

        quietly(reloaded::newGame);
        assertTrue("the axis is empty", reloaded.getHistorySave().months() <= 1);
        for (Map.Entry<String, List<? extends Number>> en
                : reloaded.getHistorySave().seriesByName().entrySet()) {
            assertTrue("  " + en.getKey() + " carries nothing from the old city",
                    en.getValue().size() <= 1);
        }

        folds(root);

        todaysPrices(root, files, city);

        firstPowerObservation();

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Monthly optional observations occupy an actual suffix, with unknown earlier months, never invented zeroes. */
    static void powerSuffix(HistorySave h, String key, double latest) {
        List<? extends Number> stored = h.seriesByName().get(key);
        int axis = h.months(), size = stored == null ? 0 : stored.size(), offset = axis - size;
        System.out.printf("  POWER %s axis=%d stored=%d offset=%d lastMonth=%s%n", key, axis, size, offset,
                axis == 0 ? "none" : h.getMonth().get(axis - 1));
        assertTrue("  " + key + " is a nonempty, non-overlong monthly suffix", h.yearlyPoints() == 0 && size > 0 && size <= axis);
        if (size == 0 || size > axis) return;
        double[] raw = h.raw(key), aligned = h.aligned(key), tail = h.alignedTail(key, axis + 1);
        boolean shape = raw.length == axis && aligned.length == axis && tail.length == axis;
        boolean prefix = shape, values = shape;
        for (int i = 0; shape && i < axis; i++) {
            if (i < offset) prefix &= Double.isNaN(raw[i]) && Double.isNaN(aligned[i]) && Double.isNaN(tail[i]);
            else {
                Number n = stored.get(i - offset);
                values &= n != null && Double.isFinite(n.doubleValue())
                        && raw[i] == n.doubleValue() && aligned[i] == n.doubleValue() && tail[i] == n.doubleValue();
            }
        }
        assertTrue("  " + key + " unobserved prefix reads NaN in all readers", prefix);
        assertTrue("  " + key + " every stored suffix entry reaches all readers", values);
        int last = Math.min(2, axis);
        assertTrue("  " + key + " short alignedTail is the actual tail", java.util.Arrays.equals(h.alignedTail(key, last),
                java.util.Arrays.copyOfRange(aligned, axis - last, axis)));
        assertTrue("  " + key + " ends at the latest native observation", shape && raw[axis - 1] == latest && Double.isFinite(latest));
    }

    /** Four fixed native months expose the first observation; no synthetic history, aging, or search for a passing month. */
    static void firstPowerObservation() throws Exception {
        System.out.println("\n--- power begins when the native observation first exists ---");
        PowerPriceCheck.City fixture = PowerPriceCheck.town("history-first-observation", false, 0);
        Game city = fixture.g;
        HistorySave h = city.getHistorySave();
        PowerView.View founding = PowerView.of(city);
        assertTrue("founding has no retained power observation or emitted history values",
                !founding.recorded() && PowerView.series(founding).isEmpty()
                        && h.seriesByName().keySet().stream().noneMatch(k -> k.startsWith("power")));
        Map<Integer, Map<String, Double>> captures = new LinkedHashMap<>();
        Map<String, Integer> first = new LinkedHashMap<>();
        int advances = 0, observed = 0, unobserved = 0, firstMonth = -1;
        Map<String, Double> latest = Map.of();
        for (int step = 0; step < 4; step++) {
            int before = h.months();
            int[] ran = {0};
            quietly(() -> ran[0] = city.simulateMonths(1));
            advances += ran[0];
            assertTrue("native first-observation advance " + step + " completed one axis entry", ran[0] == 1 && h.months() == before + 1);
            if (ran[0] != 1 || h.months() != before + 1) return;
            int index = h.months() - 1, month = h.getMonth().get(index);
            PowerView.View v = PowerView.of(city);
            latest = Map.copyOf(PowerView.series(v));
            captures.put(index, latest);
            System.out.printf("  native step=%d gameMonth=%d axisIndex=%d axisMonth=%d recorded=%s emitted=%d%n",
                    step, city.getMonth(), index, month, v.recorded(), latest.size());
            assertTrue("native history axis names the actual current month", month == city.getMonth());
            if (!v.recorded()) {
                unobserved++;
                assertTrue("unobserved native month emits no power values or stored keys", latest.isEmpty()
                        && h.seriesByName().keySet().stream().noneMatch(k -> k.startsWith("power")));
                continue;
            }
            observed++;
            if (firstMonth < 0) firstMonth = month;
            assertTrue("recorded native observation is struck at the true history month", !latest.isEmpty() && v.struckMonth() == month);
            boolean ends = true;
            for (var e : latest.entrySet()) {
                first.putIfAbsent(e.getKey(), index);
                double[] raw = h.raw(e.getKey());
                ends &= raw.length == h.months() && raw[index] == e.getValue();
            }
            assertTrue("every emitted native value reaches its true axis position", ends);
        }
        System.out.printf("  first-observation advances=%d unobserved=%d observed=%d firstRecordedMonth=%d distinctKeys=%d%n",
                advances, unobserved, observed, firstMonth, first.size());
        assertTrue("fixed native months cause both an unobserved prefix and a first recorded observation",
                advances == 4 && unobserved > 0 && observed > 0 && !first.isEmpty());
        assertTrue("native coal fixture actually emits nonzero generation, fuel and receipts",
                PowerView.of(city).rows().stream().anyMatch(r -> r.record().fuel() == Good.THERMAL_COAL
                        && r.record().kwh() > 0 && r.record().fuelUnits() > 0 && r.record().receipts() > 0));
        for (var e : first.entrySet()) {
            String key = e.getKey();
            List<? extends Number> stored = h.seriesByName().get(key);
            int index = e.getValue(), emitted = 0;
            boolean positions = true;
            double[] raw = h.raw(key);
            for (var capture : captures.entrySet()) {
                Double value = capture.getValue().get(key);
                if (value != null) { emitted++; positions &= raw[capture.getKey()] == value; }
            }
            System.out.printf("  FIRST %s index=%d month=%d emitted=%d%n", key, index, h.getMonth().get(index), emitted);
            assertTrue("  " + key + " begins at its first actual emitted month and has no missing tail",
                    stored != null && h.months() - stored.size() == index && emitted == h.months() - index && positions);
            powerSuffix(h, key, latest.getOrDefault(key, Double.NaN));
        }
        assertTrue("native first-observation fixture saved normally", city.saveGame(10, "first power observation").ok);
        city.awaitSaves();
        Game loaded = new Game(city.getGameFiles());
        quietly(() -> loaded.loadGameSave(10));
        HistorySave back = loaded.getHistorySave();
        assertTrue("first-observation native save/load restores the actual axis", loaded.getLoadFailure() == null && back.getMonth().equals(h.getMonth()));
        assertTrue("native retained power projection survives the ordinary save/load", PowerView.series(PowerView.of(loaded)).equals(latest));
        for (String key : first.keySet()) {
            assertTrue("  " + key + " ordinary reload preserves every stored observation", h.seriesByName().get(key).equals(back.seriesByName().get(key)));
            powerSuffix(back, key, latest.getOrDefault(key, Double.NaN));
        }
        loaded.awaitSaves();
    }

    /* ============ 6. past five hundred years, a year to a point (0.7.55) ============
     *
     * Jerus: "at the 500y mark, past data starts converting to yearly
     * figures, but always keep 500 recent years in monthly". A history grown
     * by hand from month 1 (HistorySave.appendMonth(), the fold
     * recordMonth() ends with) to MONTHLY_KEPT + 41 months - three whole
     * years past the months kept and five months into a fourth - every
     * series given a value its rule can be checked against: a flow a whole
     * number, so its sums are exact; a level a whole number; a rate a
     * quarter. And one company listed in month 7 (sharePrice:Late), a series
     * that begins inside a year that will fold.
     */
    static void folds(Path root) throws Exception {
        System.out.println("\n--- past five hundred years, a year to a point ---");

        int kept = HistorySave.MONTHLY_KEPT, total = kept + 3 * 12 + 5;
        java.util.Map<String, java.util.List<Double>> given = new java.util.LinkedHashMap<>();
        HistorySave[] h = { new HistorySave() };
        int firstFold = -1;
        boolean keptEnough = true, neverTooMany = true;
        for (int m = 1; m <= total; m++) {
            if (m == 7) h[0] = withSeries(h[0], "sharePrice", "Late");
            final int month = m;
            h[0].appendMonth(m, name -> {
                double v = value(name, month);
                given.computeIfAbsent(name, k -> new java.util.ArrayList<>()).add(v);
                return v;
            });
            int monthly = h[0].months() - h[0].yearlyPoints();
            if (firstFold < 0 && h[0].yearlyPoints() > 0) firstFold = m;
            if (m >= kept && monthly < kept) keptEnough = false;
            if (monthly > kept + 11) neverTooMany = false;
        }
        HistorySave grown = h[0];
        int years = (total - kept) / 12;
        System.out.printf("  %,d months grown: %d folded years and %,d months on the axis%n", total,
                grown.yearlyPoints(), grown.months() - grown.yearlyPoints());

        // The fold: when, and how many.
        close("the first year folds the month its last month leaves the newest MONTHLY_KEPT", firstFold, 12 + kept);
        assertTrue("...so the months kept never fall under MONTHLY_KEPT", keptEnough);
        assertTrue("...nor stand more than eleven over it - a year folds as soon as it can", neverTooMany);
        close("every year wholly older than the months kept is folded, and no other", grown.yearlyPoints(), years);
        close("...and the months it lived are every month grown", grown.monthsLived(), total);
        boolean axisOk = true;
        for (int y = 0; y < years; y++) {
            axisOk &= grown.getMonth().get(y) == 12 * (y + 1) && grown.monthsIn(y) == 12;
        }
        for (int i = years; i < grown.months(); i++) axisOk &= grown.getMonth().get(i) == 12 * years + 1 + (i - years);
        assertTrue("a folded year sits at its last month, holding twelve; every month after is a month", axisOk);

        // Each series, exactly by its rule.
        int checked = 0, wrong = 0, late = 0;
        String firstWrong = null;
        for (Map.Entry<String, List<? extends Number>> e : grown.seriesByName().entrySet()) {
            String name = e.getKey();
            List<Double> in = given.get(name);
            if (in == null) continue;
            double[] expect = expected(name, in, total - in.size() + 1, years);
            List<? extends Number> got = e.getValue();
            boolean same = got.size() == expect.length;
            for (int i = 0; same && i < expect.length; i++) same = got.get(i).doubleValue() == expect[i];
            checked++;
            if (!same) { wrong++; if (firstWrong == null) firstWrong = name; }
            if (in.size() < total) late++;
        }
        System.out.printf("  %d series checked, %d of them begun late; %d differ%s%n", checked, late, wrong,
                firstWrong == null ? "" : " (first: " + firstWrong + ")");
        assertTrue("every series folds exactly by its rule - a flow's months added, a level's last, a rate's averaged",
                checked > 100 && wrong == 0);
        assertTrue("...a series begun inside a folded year folds the months it has", late == 1
                && grown.raw(HistorySave.priceKey("Late"))[0] == expected(HistorySave.priceKey("Late"),
                        given.get(HistorySave.priceKey("Late")), 7, years)[0]);

        // Totals of flows are kept, to the unit.
        int flows = 0, lost = 0;
        for (String name : grown.seriesByName().keySet()) {
            if (YearBook.kindOf(name) != YearBook.Kind.FLOW || !given.containsKey(name)) continue;
            double sum = 0;
            for (double v : given.get(name)) sum += v;
            flows++;
            if (grown.total(name) != sum) lost++;
        }
        assertTrue(String.format("every flow's total over the run is the months' sum, exactly (%d flows)", flows),
                flows > 40 && lost == 0);
        double[] deathsRun = grown.runningTotal("deaths");
        double allDeaths = 0;
        for (double v : given.get("deaths")) allDeaths += v;
        close("...and a running total ends on it", deathsRun[deathsRun.length - 1], allDeaths);

        // The chart and the book read both parts.
        double[] gdp = grown.aligned("gdp"), gdpRaw = grown.raw("gdp");
        close("a folded year's flow reads a month at a time on the chart: its sum over its months",
                gdp[1], gdpRaw[1] / 12);
        close("...a level reads its year's end", grown.aligned("population")[1], given.get("population").get(23));
        close("...a rate its year's average", grown.aligned("interestRate")[1], expected("interestRate",
                given.get("interestRate"), 1, years)[1]);
        close("a pointer by a folded year's point, at its year's end, reads that year",
                ChartModel.nearest(grown.getMonth(), 22), 1);
        double[] reach = ChartModel.reach(gdp, grown.getMonth(), 1, 12 * years + 24);
        assertTrue("...and a window across the fold reaches both parts",
                reach[0] <= Math.min(gdp[0], gdp[years]) && reach[1] >= Math.max(gdp[0], gdp[years + 23]));
        close("twelve months back from a folded year is the year before it", grown.back(2, 12), 1);
        close("...and from the first month after the fold, the last folded year", grown.back(years, 12), years - 1);
        double[] rolling = YearBook.realGdpYear(grown);
        boolean runsOn = true;
        for (int i = 0; i < rolling.length; i++) runsOn &= !Double.isNaN(rolling[i]);
        assertTrue("the rolling year of real output runs on across the fold, never blank", runsOn);
        double[] real = YearBook.realGdp(grown);
        close("...the first month after it the last folded year's eleven months at their average, and its own",
                rolling[years], real[years - 1] * 11 + real[years]);
        YearBook.Table book = YearBook.yearBook(grown, Currency.fromCityName("Arden")).table();
        List<String> header = book.header(), second = book.rows().get(1);
        int gdpCol = header.indexOf("gdp"), nCol = header.indexOf("n");
        close("the year book's row for a folded year holds its twelve months", Double.parseDouble(second.get(nCol)), 12);
        assertTrue("...and its gdp is the year's sum", second.get(gdpCol).equals(bookCell(gdpRaw[1])));
        close("...and the book has a row a year: the folded ones and the months after", book.rows().size(),
                (total + 11) / 12);

        // A save gives it all back, and folds on the same way.
        GameFiles files = new GameFiles(root.resolve("fold"), root.resolve("fold-no-legacy"));
        assertTrue("the folded history saves", grown.saveHistory(files, 10).ok);
        HistorySave back = new HistorySave();
        back.restoreFrom(new com.google.gson.Gson().fromJson(Files.readString(files.historyFile(10)), HistorySave.class));
        assertTrue("...and loads: the axis, its folded years and every series, as they were",
                back.getMonth().equals(grown.getMonth()) && back.yearlyPoints() == grown.yearlyPoints()
                        && back.seriesByName().equals(grown.seriesByName()));
        for (int m = total + 1; m <= total + 12; m++) {
            final int month = m;
            grown.appendMonth(m, name -> value(name, month));
            back.appendMonth(m, name -> value(name, month));
        }
        assertTrue("...and a year on, both have folded the next year alike",
                back.yearlyPoints() == years + 1 && back.getMonth().equals(grown.getMonth())
                        && back.seriesByName().equals(grown.seriesByName()));

        HistorySave older = new com.google.gson.Gson().fromJson("{\"month\":[1,2,3],\"gdp\":[1.0,2.0,3.0]}", HistorySave.class);
        assertTrue("a history from before 0.7.55 has no folded years: every entry a month",
                older.yearlyPoints() == 0 && older.monthsIn(0) == 1 && older.monthsLived() == 3);
    }

    /** The value grown for a series in a month: a flow and a level whole numbers, a rate a quarter. */
    static double value(String name, int month) {
        int code = Math.floorMod(name.hashCode(), 97);
        YearBook.Kind kind = YearBook.kindOf(name);
        if (kind == YearBook.Kind.FLOW) return 1 + Math.floorMod(month * 7 + code, 13);
        if (kind == YearBook.Kind.RATE) return (1 + Math.floorMod(month + code, 9)) * .25;
        return 1000 + month + code;
    }

    /**
     * What a series must hold after the fold, worked out here and not by the
     * fold: its months from `from`, the first `years` calendar years folded
     * by its rule - a flow added oldest first, a rate's months added and
     * divided, a level's last - and the rest as given.
     */
    static double[] expected(String name, List<Double> in, int from, int years) {
        List<Double> out = new java.util.ArrayList<>();
        YearBook.Kind kind = YearBook.kindOf(name);
        int i = 0;
        for (int y = 1; y <= years; y++) {
            double sum = 0, last = Double.NaN;
            int n = 0;
            for (; i < in.size() && from + i <= 12 * y; i++) { sum += in.get(i); last = in.get(i); n++; }
            if (n == 0) continue;
            out.add(kind == YearBook.Kind.FLOW ? sum : kind == YearBook.Kind.RATE ? sum / n : last);
        }
        for (; i < in.size(); i++) out.add(in.get(i));
        double[] a = new double[out.size()];
        for (int k = 0; k < a.length; k++) a[k] = out.get(k);
        return a;
    }

    /** The year book's cell for a figure, as the book prints it: its own compact(), through a one-row history. */
    static String bookCell(double v) {
        HistorySave one = new com.google.gson.Gson().fromJson("{\"month\":[12],\"gdp\":[" + v + "]}", HistorySave.class);
        YearBook.Table t = YearBook.yearBook(one, Currency.fromCityName("Arden")).table();
        return t.rows().get(0).get(t.header().indexOf("gdp"));
    }

    /** A history with one more company's series, empty, so it begins with the next month appended. */
    static HistorySave withSeries(HistorySave h, String map, String key) {
        com.google.gson.Gson gson = new com.google.gson.Gson();
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(gson.toJson(h)).getAsJsonObject();
        if (!json.has(map)) json.add(map, new com.google.gson.JsonObject());
        json.getAsJsonObject(map).add(key, new com.google.gson.JsonArray());
        return gson.fromJson(json, HistorySave.class);
    }

    /** Removes one "name": [ ... ] entry from the history JSON. */
    static String dropSeries(String json, String name) {
        String key = "\"" + name + "\":";
        int at = json.indexOf(key);
        if (at < 0) return json;
        int close = json.indexOf(']', at);
        int end = close + 1;
        if (end < json.length() && json.charAt(end) == ',') end++;
        return json.substring(0, at) + json.substring(end);
    }

    /* ============ 7. the eight prices write at today's prices (CU2, REV-CU2-1) ============
     *
     * HistorySave.roundSig keeps six figures of a price at today's prices and
     * multiplies them back by PriceLevel.FACTOR, a long decimal, so the double
     * it records takes seventeen digits to write: 935,000 characters of a
     * default playtest's history. The file now writes those six figures,
     * {"todays":[...]}, and the load multiplies them again, which is the
     * product roundSig made - the same bits, not the same to six figures. A
     * series a reform or a fold moved off that grid is written as the plain
     * array it always was. The city played here is section 1's, its history
     * saved and reloaded the way the game does; the one moved off the grid is
     * that history after a reform by a seventh.
     */
    static final String[] PRICES = {"minimumWage", "landPrice", "foodPrice", "materialsPrice", "orePrice", "rentPrice"};

    /** The eight prices of a history, by the name seriesByName() gives them: the six, and each company's share price and value. */
    static Map<String, List<? extends Number>> eight(HistorySave h) {
        Map<String, List<? extends Number>> all = h.seriesByName(), found = new LinkedHashMap<>();
        for (String name : PRICES) found.put(name, all.get(name));
        for (Map.Entry<String, List<? extends Number>> e : all.entrySet()) {
            if (e.getKey().startsWith("sharePrice:") || e.getKey().startsWith("shareValue:")) found.put(e.getKey(), e.getValue());
        }
        return found;
    }

    /** One of the eight in the written file: the field by its name, or a company's series inside its share field. */
    static JsonElement inFile(JsonObject file, String name) {
        int colon = name.indexOf(':');
        if (colon < 0) return file.get(name);
        JsonElement byCompany = file.get(name.substring(0, colon));
        return byCompany == null || !byCompany.isJsonObject() ? null : byCompany.getAsJsonObject().get(name.substring(colon + 1));
    }

    /** The significant digits a number is written with: its mantissa's, from the first to the last that is not a zero. */
    static int figures(String number) {
        String digits = number.toLowerCase().split("e")[0].replace("-", "").replace(".", "");
        int first = 0, last = digits.length();
        while (first < last && digits.charAt(first) == '0') first++;
        while (last > first && digits.charAt(last - 1) == '0') last--;
        return last - first;
    }

    /** Two series the same length, with a null where the other has one and every other entry the same double to the bit. */
    static boolean sameBits(List<? extends Number> a, List<? extends Number> b) {
        if (a == null || b == null || a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            Number x = a.get(i), y = b.get(i);
            if (x == null || y == null) {
                if (x != y) return false;
            } else if (Double.doubleToLongBits(x.doubleValue()) != Double.doubleToLongBits(y.doubleValue())) {
                return false;
            }
        }
        return true;
    }

    /** How many of the eight in `a` are, to the bit, what `b` has under the same name. */
    static int sameEight(HistorySave a, HistorySave b) {
        int same = 0;
        Map<String, List<? extends Number>> other = eight(b);
        for (Map.Entry<String, List<? extends Number>> e : eight(a).entrySet()) {
            if (sameBits(e.getValue(), other.get(e.getKey()))) same++;
        }
        return same;
    }

    /** The text the eight take when each is written by Gson as the plain array of its doubles, the form before CU2. */
    static int plainBytes(HistorySave h) {
        Gson gson = new Gson();
        int bytes = 0;
        for (List<? extends Number> series : eight(h).values()) bytes += gson.toJson(series).getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        return bytes;
    }

    /** The text the eight take in the file as written: each one's element, as Gson writes it. */
    static int writtenBytes(HistorySave h, JsonObject file) {
        Gson gson = new Gson();
        int bytes = 0;
        for (String name : eight(h).keySet()) bytes += gson.toJson(inFile(file, name)).getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        return bytes;
    }

    static void todaysPrices(Path root, GameFiles files, Game city) throws Exception {
        System.out.println("\n--- the eight prices write at today's prices ---");
        HistorySave live = city.getHistorySave();
        Map<String, List<? extends Number>> prices = eight(live);
        int companies = (int) prices.keySet().stream().filter(n -> n.startsWith("sharePrice:")).count();

        boolean valued = true, onGrid = true;
        for (List<? extends Number> series : prices.values()) {
            valued &= series != null && !series.isEmpty();
            if (series != null) for (Number n : series) onGrid &= n == null || HistorySave.onTodaysGrid(n.doubleValue());
        }
        assertTrue("the played city has values in each of the eight series (" + prices.size() + " of them, " + companies + " companies listed)",
                valued && companies > 0 && prices.size() == PRICES.length + 2 * companies);
        assertTrue("...and every one of them is a figure roundSig recorded: it is on the grid of six figures at today's prices", onGrid);
        Map<String, long[]> before = new LinkedHashMap<>();
        for (Map.Entry<String, List<? extends Number>> e : prices.entrySet()) {
            before.put(e.getKey(), e.getValue().stream().mapToLong(n -> n == null ? 0L : Double.doubleToLongBits(n.doubleValue())).toArray());
        }

        // (a) saved
        assertTrue("the city saved to slot 2 of its scratch files", city.saveGame(2, "todays").ok);
        Path written = files.historyFile(2);
        JsonObject file = JsonParser.parseString(Files.readString(written)).getAsJsonObject();
        int todays = 0, figuresMost = 0, entries = 0;
        for (Map.Entry<String, List<? extends Number>> e : prices.entrySet()) {
            JsonElement at = inFile(file, e.getKey());
            if (at == null || !at.isJsonObject() || !at.getAsJsonObject().has("todays") || !at.getAsJsonObject().get("todays").isJsonArray()) continue;
            com.google.gson.JsonArray array = at.getAsJsonObject().getAsJsonArray("todays");
            if (array.size() != e.getValue().size()) continue;
            todays++;
            for (JsonElement n : array) {
                if (n.isJsonNull()) continue;
                entries++;
                figuresMost = Math.max(figuresMost, figures(n.getAsString()));
            }
        }
        assertTrue("each of the eight is written as {\"todays\":[...]} with an entry for every month (" + todays + " of " + prices.size() + ")", todays == prices.size());
        assertTrue("...and a todays entry is written with at most " + HistorySave.SHARE_PRICE_DIGITS + " significant digits (" + entries + " entries, " + figuresMost + " at most)",
                entries > 0 && figuresMost <= HistorySave.SHARE_PRICE_DIGITS);

        // (b) reloaded the way the game reloads: the save and its history, from the slot
        Game again = new Game(files);
        quietly(() -> again.loadGameSave(2));
        HistorySave reloaded = again.getHistorySave();
        int reloadedSame = sameEight(live, reloaded);
        assertTrue("reloaded, the axis and each of the eight series are the live ones, to the bit: nulls in place, the same length, every company's two ("
                + reloadedSame + " of " + prices.size() + ")", reloaded.months() == live.months() && reloadedSame == prices.size());
        boolean unmoved = true;
        for (Map.Entry<String, List<? extends Number>> e : eight(live).entrySet()) {
            long[] was = before.get(e.getKey());
            unmoved &= was.length == e.getValue().size();
            for (int i = 0; unmoved && i < was.length; i++) {
                Number n = e.getValue().get(i);
                unmoved &= (n == null ? 0L : Double.doubleToLongBits(n.doubleValue())) == was[i];
            }
        }
        assertTrue("...and saving moved no bit of the series held in memory", unmoved);

        // the kept read of a slot (LB4): the prices the measures read come off the file as primitives, to the bit
        HistorySave slim = HistorySave.readKept(new StringReader(Files.readString(written)), SavedCities.KEPT);
        int keptPrices = 0, keptSame = 0;
        for (String name : PRICES) {
            if (!SavedCities.KEPT.contains(name)) continue;
            keptPrices++;
            List<? extends Number> read = slim.seriesByName().get(name);
            if (read instanceof HistorySave.PackedDoubles && sameBits(live.seriesByName().get(name), read)) keptSame++;
        }
        assertTrue("the slim read of the file holds the prices the measures keep as a PackedDoubles, to the bit (" + keptSame + " of " + keptPrices + ")",
                keptPrices >= 2 && keptSame == keptPrices);

        // (c) off the grid: the same history after a reform that scales every price by a seventh (a decimal shift can leave a short figure on the grid)
        HistorySave moved = new HistorySave();
        moved.restoreFrom(live);
        moved.redenominate(1.0 / 7);
        int off = 0;
        for (Number n : moved.seriesByName().get("minimumWage")) if (n != null && !HistorySave.onTodaysGrid(n.doubleValue())) off++;
        assertTrue("fixture: after a reform by a seventh " + off + " of the wage's " + moved.seriesByName().get("minimumWage").size() + " figures are off the grid", off > 0);
        GameFiles reformed = new GameFiles(root.resolve("todays"), root.resolve("todays-no-legacy"));
        assertTrue("the reformed history saved", moved.saveHistory(reformed, 10).ok);
        String reformedText = Files.readString(reformed.historyFile(10));
        JsonObject reformedFile = JsonParser.parseString(reformedText).getAsJsonObject();
        assertTrue("the wage, off the grid, is written as a plain array, the one Gson writes of its list",
                reformedFile.get("minimumWage").isJsonArray()
                        && new Gson().toJson(reformedFile.get("minimumWage")).equals(new Gson().toJson(moved.seriesByName().get("minimumWage"))));
        assertTrue("the ground's price, which a reform does not scale, is still {\"todays\":[...]}", reformedFile.get("landPrice").isJsonObject()
                && reformedFile.get("landPrice").getAsJsonObject().has("todays"));
        HistorySave movedBack = new HistorySave();
        movedBack.restoreFrom(new Gson().fromJson(reformedText, HistorySave.class));
        int movedSame = sameEight(moved, movedBack);
        assertTrue("every one of the eight reads back after the reform as it was, to the bit (" + movedSame + " of " + prices.size() + ")", movedSame == prices.size());

        // a file as every build before CU2 wrote it still reads
        HistorySave plain = new Gson().fromJson("{\"month\":[1,2,3],\"minimumWage\":[0.5,null,0.123456789],\"sharePrice\":{\"Bank\":[1.5,2.5]},"
                + "\"shareValue\":{\"Bank\":{\"todays\":[null,2]}}}", HistorySave.class);
        assertTrue("a plain array as older builds wrote it reads as it is, a null where it was, and a todays series with a null in it reads as its figures times FACTOR",
                plain.seriesByName().get("minimumWage").equals(java.util.Arrays.asList(0.5, null, 0.123456789))
                        && plain.seriesByName().get(HistorySave.priceKey("Bank")).equals(List.of(1.5, 2.5))
                        && plain.seriesByName().get(HistorySave.valueKey("Bank")).equals(java.util.Arrays.asList(null, 2 * PriceLevel.FACTOR)));

        // (d) shorter
        int shortBytes = writtenBytes(live, file), longBytes = plainBytes(live);
        System.out.printf("  the eight series' text: %,d bytes at today's prices, %,d bytes as plain arrays of the same values (%.1f%% shorter); the history file %,d bytes%n",
                shortBytes, longBytes, 100.0 * (longBytes - shortBytes) / longBytes, Files.size(written));
        assertTrue("the eight series take fewer bytes written at today's prices (" + shortBytes + ") than as plain arrays of the same values (" + longBytes + ")", shortBytes < longBytes);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
