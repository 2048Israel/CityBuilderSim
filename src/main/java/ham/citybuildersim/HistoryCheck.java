package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

        Path root = Files.createTempDirectory("history");
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
        boolean primeOverDial = true, anyFee = false;
        for (int i = 0; i < h.months(); i++) {
            // Every part of the price is at least zero and the first is the
            // dial itself, so prime is never under it (rounding allowed).
            if (primes[i] < Math.max(0, dials[i]) - 1e-4) primeOverDial = false;
            if (h.aligned("bankFees")[i] > 0) anyFee = true;
        }
        assertTrue("prime is never under the dial, in any month", primeOverDial);
        assertTrue("...and the founding bank charged a fee in some month", anyFee);

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

        System.out.printf("  last month: ratio %.4f, target %.4f, allowance %.2f, provisions %.2f, dividends %.2f, ROE %.4f%n",
                h.aligned("bankCapitalRatio")[last], h.aligned("bankCapitalTarget")[last],
                h.aligned("bankAllowance")[last], h.aligned("bankProvisions")[last],
                h.aligned("bankDividends")[last], h.aligned("bankReturnOnEquity")[last]);
        double weighted = lender.getWeightedBook();
        assertTrue("the last month's bankCapitalRatio is Bank.capitalRatio(), clamped at ten",
                Math.abs(h.aligned("bankCapitalRatio")[last]
                        - (weighted > 0 ? Math.min(10, lender.capitalRatio()) : 10)) <= 5e-5 + 1e-12);
        assertTrue("...its bankCapitalTarget is the target the bank chose",
                Math.abs(h.aligned("bankCapitalTarget")[last] - lender.capitalTarget()) <= 5e-5 + 1e-12);
        assertTrue("...its bankAllowance is what it has set aside, to the cent",
                Math.abs(h.aligned("bankAllowance")[last] - lender.getAllowance())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lender.getAllowance()));
        assertTrue("...its bankProvisions is the month's provision, to the cent",
                Math.abs(h.aligned("bankProvisions")[last] - lender.provisions())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lender.provisions()));
        assertTrue("...its bankDividends is what it paid its owners, to the cent",
                Math.abs(h.aligned("bankDividends")[last] - lender.getDividendsPaid())
                        <= MoneyAudit.tolerance(.005 + 1e-9, lender.getDividendsPaid()));
        assertTrue("...and its bankReturnOnEquity is the month's return, a year, clamped at ten",
                Math.abs(h.aligned("bankReturnOnEquity")[last]
                        - Math.max(-10, Math.min(10, lender.returnOnEquity()))) <= 5e-5 + 1e-12);
        double[] targets = h.aligned("bankCapitalTarget");
        boolean young = true;
        for (int i = 0; i < h.months(); i++) {
            if (!Double.isNaN(targets[i])
                    && Math.abs(targets[i] - (Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER)) > 5e-5 + 1e-12) young = false;
        }
        assertTrue("fixture: the founding bank has lived through no bad year", lender.getWorstLossRate() <= Bank.CONSERVATION_BUFFER);
        assertTrue("...so it targets the minimum and the conservation buffer in every month", young);
        /*
         * ...AND THE OTHER MEASURE (A7, 0.7.46): the leverage ratio - which
         * can bind where the clamped capital ratio reads ten - and the target
         * the bank holds on it, every month, at the getters' own figures.
         */
        assertTrue("fixture: the bank has something on its sheet, so its leverage ratio is a figure",
                lender.exposure() > 0 && lender.leverageRatio() < 10);
        // ...and the outbreak the year book names an epidemic on (A8, 0.7.46), as Health reads it.
        assertTrue("the outbreak series is Health's outbreak, 0 between them, every month",
                h.monthsRecorded("outbreak") == h.months()
                        && Math.abs(h.aligned("outbreak")[last] - city.getHealth().getOutbreakSeverity()) <= 5e-5 + 1e-12);
        assertTrue("the leverage ratio and its target are recorded every month as the bank reads them",
                h.monthsRecorded("bankLeverageRatio") == h.months() && h.monthsRecorded("bankLeverageTarget") == h.months()
                        && Math.abs(h.aligned("bankLeverageRatio")[last] - Math.min(10, lender.leverageRatio())) <= 5e-5 + 1e-12
                        && Math.abs(h.aligned("bankLeverageTarget")[last] - lender.leverageTarget()) <= 5e-5 + 1e-12);

        /* ============ 2f. the month's graduates (A6, 0.7.46) ============

           The series recorded the movement between education bands, whose
           +1s and -1s net to nothing, so it read zero in every city. It is
           the month's gains now (Education.gainedThisMonth()), which can never
           be negative. CAUSED: a funded town of EducationCheck's with every
           basic stage built, whose high schools hand out diplomas within a
           few months.
           ================================================================= */
        System.out.println("\n--- the month's graduates are the people who gained a qualification ---");
        Game[] schooledBox = new Game[1];
        quietly(() -> {
            schooledBox[0] = EducationCheck.city(null);
            EducationCheck.build(schooledBox[0], "Elementary School", 2);
            EducationCheck.build(schooledBox[0], "Middle School", 2);
            EducationCheck.build(schooledBox[0], "High School", 2);
            schooledBox[0].simulateMonths(12);
        });
        Game schooled = schooledBox[0];
        double[] graduated = schooled.getHistorySave().aligned("graduates");
        boolean neverNegative = true;
        for (double v : graduated) if (v < 0) neverNegative = false;
        assertTrue("fixture: the town's high schools handed out diplomas this month",
                schooled.getEducation().getNewDiplomas() > 0);
        assertTrue("the graduates series is never negative", neverNegative);
        assertTrue("...and its last month is what the schools gained, to the history's rounding",
                Math.abs(graduated[graduated.length - 1] - schooled.getEducation().gainedThisMonth()) <= .005 + 1e-9
                        && graduated[graduated.length - 1] > 0);

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
        // ...nor 0.7.35's parity beside the rate.
        for (String added : new String[]{"policyRate", "bankPrime", "bankDepositRate", "bankFees",
                "bankCapitalRatio", "bankCapitalTarget", "bankAllowance", "bankProvisions",
                "bankDividends", "bankReturnOnEquity", "fxParity"}) {
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
        quietly(() -> old076.simulateMonths(2));
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

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
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

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
