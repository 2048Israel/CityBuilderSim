package ham.citybuildersim;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Proves the year book folds each series the way that series has to be folded,
 * and that the file says so. Not part of the game.
 *
 * WHY THIS IS WORTH A HARNESS
 *
 * The year book's whole value is that somebody who has never seen this game can
 * read a run off it. That makes a wrong fold worse than no file at all: a
 * population summed over twelve months reads as a city twelve times its size,
 * and reads as a FACT, with no way for the reader to catch it. Every assertion
 * here is on the text the reader actually gets, not on an internal the reader
 * never sees.
 *
 * THE FIXTURES ARE BUILT AS SAVED HISTORY, through Gson, on purpose. It is the
 * same path a loaded slot takes, so the harness cannot pass on a history that
 * could never come off disk - and it lets a series be given an exact shape,
 * which is what makes "the sum is 78 and not 77" assertable at all.
 *
 * THE ONE THING IT CANNOT CHECK is whether a rule is the RIGHT rule - that
 * bankWriteOffs really is a monthly flow and householdSavings really is a
 * running total. That is read off HistorySave.recordMonth and written down in
 * YearBook.rules(); what this can do, and does, is refuse to let a series exist
 * with no rule at all.
 *
 * @author Jerus
 */
public class YearBookCheck {

    static int fails = 0;
    static int checks = 0;

    /** What HistorySave.round2() can lose on a figure it stores. */
    private static final double A_CENT = 0.005;

    /** What storing the pool as a whole person can lose on a figure derived from it. */
    private static final double HALF_A_PERSON = 0.51;

    public static void main(String[] args) {

        everySeriesHasARule();
        aFlowIsAdded();
        aLevelIsTheLastMonth();
        aRateIsAveragedAndItsWorstMonthIsKept();
        aSeriesThatStartedLateStaysBlank();
        aShortLastRowSaysSo();
        decadesAreTenYears();
        theEpisodeListFindsAnEpisode();
        theFileNeverWritesADecimalComma();
        theExportPathsAreBesideTheSaves();
        theUnemploymentColumnIsThePool();
        theCurrencyNoteReadsTheRightWay();
        theBookAgreesWithTheModelOnAPlayedCity();
        theEpisodesAreTheOnesTheFixtureCaused();

        System.out.printf("%n%d assertions: %s%n", checks,
                fails == 0 ? "the year book folds every series by its own rule." : fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /* ==================================================================
       1 - EVERY SERIES HAS A RULE

       The fixture is a city that has actually been played, because three of
       the families - crime by cause, households by shape, the share registers
       - do not exist in a HistorySave until something has caused them. A rule
       table checked against an EMPTY history would pass while covering none of
       them, which is this project's most-repeated mistake in another costume.
       ================================================================== */
    private static void everySeriesHasARule() {
        Game game = new Game(GameFiles.scratch("year-book-check"));
        game.newGame();
        for (int i = 0; i < 14; i++) game.toggleNextMonth();

        HistorySave h = game.getHistorySave();

        boolean sawCrime = false, sawShapes = false, sawShares = false;
        for (String name : h.seriesByName().keySet()) {
            if (name.startsWith("crime:")) sawCrime = true;
            if (name.startsWith("households:")) sawShapes = true;
            if (name.startsWith(HistorySave.priceKey(""))) sawShares = true;
        }
        yes("the fixture actually produced crime-by-cause series", sawCrime);
        yes("the fixture actually produced households-by-shape series", sawShapes);
        yes("the fixture actually produced a share register", sawShares);

        List<String> unruled = YearBook.unruled(h);
        if (!unruled.isEmpty()) {
            System.out.println("  FAIL  no aggregation rule for: " + String.join(", ", unruled));
            System.out.println("        add it to YearBook.rules() - a series with no rule is folded as a");
            System.out.println("        level, which is silently wrong for every flow.");
            fails++;
        }
        checks++;

        /* And the rules do not name series that do not exist. */
        for (String name : h.seriesByName().keySet()) {
            same("kind is declared for " + name, YearBook.kindOf(name) != null, true);
            same("a note is declared for " + name, YearBook.noteOf(name) != null, true);
        }
    }

    /* ==================================================================
       2 - A FLOW IS ADDED
       ================================================================== */
    private static void aFlowIsAdded() {
        HistorySave h = built(24, "gdp", ramp(24));
        String text = YearBook.years(h);

        // 1+2+...+12 and 13+14+...+24, against the arithmetic rather than a literal.
        same("year 1 gdp is its twelve months added", cell(text, "gdp", 1), sum(1, 12));
        same("year 2 gdp is its twelve months added", cell(text, "gdp", 2), sum(13, 24));
        yes("gdp is marked as a flow", text.contains("gdp                   [+]"));
    }

    /* ==================================================================
       3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN
       ================================================================== */
    private static void aLevelIsTheLastMonth() {
        HistorySave h = built(24, "population", ramp(24));
        String text = YearBook.years(h);

        same("year 1 population is December's", cell(text, "population", 1), 12.0);
        same("year 2 population is December's", cell(text, "population", 2), 24.0);
        yes("population is marked as a level", text.contains("population            [=]"));
    }

    /* ==================================================================
       4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES

       The second half is the point of the WITHIN block: a year that averaged
       6.5% sickness and peaked at 12% is a different year from one that sat
       at 6.5% all twelve months, and the table alone cannot tell them apart.
       ================================================================== */
    private static void aRateIsAveragedAndItsWorstMonthIsKept() {
        double[] rate = new double[24];
        for (int i = 0; i < 24; i++) rate[i] = (i + 1) / 100.0;
        HistorySave h = built(24, "sickRate", rate);
        String text = YearBook.years(h);

        same("year 1 sick rate is the mean of its months", cell(text, "sickRate", 1), sum(1, 12) / 12 / 100.0);
        same("the year's worst month is kept", cell(text, "sickRate.hi", 1), 0.12);
        same("and its best", cell(text, "sickRate.lo", 1), 0.01);
        yes("sickRate is marked as a rate", text.contains("sickRate              [~]"));
    }

    /* ==================================================================
       5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO

       HistorySave's own rule, and the reason it exists: "we were not counting"
       is not the same claim as "it was zero". A flow row is blank if ANY of
       its months is missing, because a sum of six months is not a year - the
       one place the year book is stricter than the graph.
       ================================================================== */
    private static void aSeriesThatStartedLateStaysBlank() {
        // Twenty-four months of life, but crime only recorded for the last eighteen:
        // year 1 is six months short, year 2 is complete.
        double[] late = new double[18];
        for (int i = 0; i < 18; i++) late[i] = 100;
        HistorySave h = built(24, "stolen", late);
        String text = YearBook.years(h);

        yes("a flow row missing months is blank", blank(text, "stolen", 1));
        same("and the complete row is the sum", cell(text, "stolen", 2), 1200.0);
        yes("the file says what a blank means",
                text.contains("A BLANK cell means the city was alive but was not recording that number yet"));
    }

    /* ==================================================================
       6 - A SHORT LAST ROW SAYS SO
       ================================================================== */
    private static void aShortLastRowSaysSo() {
        HistorySave h = built(25, "gdp", ramp(25));
        String text = YearBook.years(h);

        same("a full year covers twelve months", cell(text, "n", 1), (double) YearBook.MONTHS_A_YEAR);
        same("the stub year says it is one month", cell(text, "n", 3), 1.0);
        same("and its flow is that one month", cell(text, "gdp", 3), 25.0);
    }

    /* ==================================================================
       7 - A DECADE IS TEN YEARS OF MONTHS
       ================================================================== */
    private static void decadesAreTenYears() {
        HistorySave h = built(130, "gdp", ramp(130));
        String text = YearBook.decades(h);

        same("a full decade covers ten years of months",
                cell(text, "n", 1), (double) YearBook.MONTHS_A_DECADE);
        same("the stub decade carries the remainder", cell(text, "n", 2), 10.0);
        same("the first decade's flow is its months added", cell(text, "gdp", 1), sum(1, 120));
        yes("the decade book calls its rows decades", text.contains("the decade, 1 is the founding decade"));
    }

    /* ==================================================================
       8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED

       A yearly average is exactly where a one-month failure hides: a bank that
       was under water in March and recapitalised in April averages to a
       perfectly solvent year.
       ================================================================== */
    private static void theEpisodeListFindsAnEpisode() {
        double[] equity = new double[24];
        for (int i = 0; i < 24; i++) equity[i] = 1000;
        equity[6] = -50;                       // month 7, and nothing else
        HistorySave h = built(24, "bankEquity", equity);
        String text = YearBook.years(h);

        yes("the episode list names the failure", text.contains("equity under water in 1 month"));
        yes("and says which month it was", text.contains("month 7"));
        yes("the year's own row hides it, which is why the list exists",
                cell(text, "bankEquity", 1) == 1000.0);
    }

    /* ==================================================================
       9 - NO DECIMAL COMMAS, EVER

       A tab-separated file written on a machine set to French turns every
       "1.5" into "1,5". Nothing in the file would look wrong; every number
       after the first would be read as two.
       ================================================================== */
    private static void theFileNeverWritesADecimalComma() {
        Locale was = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRANCE);
            same("a decimal point survives a French locale", YearBook.compact(1.5), "1.5");
            same("...and so does a small one", YearBook.compact(0.0625), "0.0625");
            same("...and a big one", YearBook.compact(1.23e9), "1.23e9");
            same("a thousand is written without a separator", YearBook.compact(1234.0), "1234");
            same("nothing is recorded is not zero", YearBook.compact(Double.NaN), "");
            same("and zero is zero", YearBook.compact(0), "0");

            /*
             * Only the DATA rows. The prose above them is full of honest
             * commas - "GDP by expenditure, C+I+G+NX, in thousands" - and a
             * first cut of this assertion read the whole file and failed on
             * the legend it had just asked for.
             */
            HistorySave h = built(24, "gdp", ramp(24));
            int rows = 0;
            for (String line : YearBook.years(h).split("\n")) {
                if (!line.contains("\t") || !Character.isDigit(line.charAt(0))) continue;
                rows++;
                yes("no comma in a data row: " + line, !line.contains(","));
            }
            yes("and there were data rows to look at", rows > 0);
        } finally {
            Locale.setDefault(was);
        }
    }

    /* ==================================================================
       10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM
       ================================================================== */
    private static void theExportPathsAreBesideTheSaves() {
        GameFiles files = GameFiles.scratch("year-book-paths");
        yes("the year book sits in the game's own folder",
                files.yearBookFile().getParent().equals(files.getDirectory()));
        yes("so does the decade book",
                files.decadeBookFile().getParent().equals(files.getDirectory()));
        yes("and they are not the same file",
                !files.yearBookFile().equals(files.decadeBookFile()));
    }

    /* ==================================================================
       FIXTURES AND READING
       ================================================================== */

    /* ==================================================================
       11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE

       Found on slot 3 (2026-09-15): (workforce - jobs) / workforce read 14%
       for seventy years of a city whose out-of-work ledgers were empty and
       whose EI had paid nothing, because the workforce still holds the
       students and the prisoners and the jobs are posts offered rather than
       filled. The pool is recorded now, and the column is struck from it the
       way the People screen strikes its own figure. Two histories: one with
       the pool recorded, one built by hand the way an old save reads, where
       the labour force less the posts is the fallback.
       ================================================================== */
    private static void theUnemploymentColumnIsThePool() {
        // A played city: the pool the People screen shows is what the column reads.
        Game game = new Game(GameFiles.scratch("year-book-check-pool"));
        game.newGame();
        for (int i = 0; i < 14; i++) game.toggleNextMonth();
        HistorySave h = game.getHistorySave();
        PopulationManager people = game.getPopulationManager();
        List<Integer> pool = h.getOutOfWork();
        same("the history records the pool the People screen shows",
                pool.get(pool.size() - 1).doubleValue(), (double) people.getUnemployed());

        // A history with the pool: 100 adults, 20 studying, 70 posts of which
        // 65 filled, 15 out of work - 15 over a labour force of 80, and NOT
        // (100 - 70) / 100, which is what the first edition would have said.
        String text = YearBook.years(history(100, 20, 70, 15));
        same("with the pool recorded, the pool over the labour force",
                cell(text, "unemployment", 1), 15.0 / 80.0);
        yes("the column says what it is", text.contains("people out of work over the labour force"));

        // An old history, no pool recorded: the labour force less the posts,
        // 10 over 80 - the students still come out of the denominator.
        same("without the pool, the labour force less the posts, over the labour force",
                cell(YearBook.years(history(100, 20, 70, -1)), "unemployment", 1), 10.0 / 80.0);
    }

    /* ==================================================================
       13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED

       THIS IS THE ASSERTION THAT WOULD HAVE CAUGHT ALL OF IT.

       The first edition shipped with 277 assertions and three wrong columns.
       Every one of those assertions tested that the FOLD was right - a flow
       sums, a level takes December, a rate averages - and not one tested that
       an input meant what its own note claimed. Arithmetic on the wrong
       number is still wrong, and a harness that only checks the arithmetic
       will say so cheerfully for three hundred years of city.

       So this compares the book's derived figures against the model's own,
       on a real played city, at the month the history last recorded. Nothing
       here is arithmetic the book did; it is all "does the file say what the
       game says".

       AND THE PREMISE IS ASSERTED FIRST. A city with nobody studying and
       every post filled cannot tell the right denominator from the wrong one
       - workforce would equal the labour force and the broken formula would
       pass. So the fixture is checked for the condition it exists to cause,
       and the old formula is struck here and required to DISAGREE. If the
       founding order ever changes so that no one studies in five years, this
       fails loudly instead of quietly testing nothing.
       ================================================================== */
    private static void theBookAgreesWithTheModelOnAPlayedCity() {
        /*
         * A city with money, room, homes and a college, because the founding
         * order has nobody studying for the first sixty months and a city
         * with no students cannot tell workforce from labour force. The grant
         * is deliberately small - EducationCheck's own comment records what
         * four billion square feet did to the land price.
         */
        Game game = new Game(GameFiles.scratch("year-book-check-model"));
        game.newGame();
        game.getGovernmentInvestor().spend(-900_000_000);
        game.getLandManager().setOwnedSqFt(game.getLandManager().getOwnedSqFt() + 400_000_000L);
        put(game, "Low-Rise Apartments", 120);
        put(game, "Small Grocery Store", 14);
        put(game, "Coal Power Plant", 4);
        put(game, "Water Treatment Plant", 4);
        put(game, "Elementary School", 14);
        put(game, "Middle School", 14);
        put(game, "High School", 10);
        put(game, "Community College", 6);
        for (int i = 0; i < 60; i++) game.toggleNextMonth();

        HistorySave h = game.getHistorySave();
        PopulationManager people = game.getPopulationManager();
        int last = h.months() - 1;

        /* ------------------------- the premise ------------------------- */
        yes("the fixture has somebody studying, or the two denominators are the same number",
                people.getStudyingTotal() > 0);
        yes("the fixture offers posts it has not filled, or jobs and filled posts agree by luck",
                people.getTotalJobs() != people.getJobsFilled());

        double[] workforce = h.aligned("workforce");
        double[] jobs      = h.aligned("jobs");
        double theOldWay = Math.max(0, (workforce[last] - jobs[last]) / workforce[last]);
        double theModel  = people.getUnemployed() / people.getLabourForce();
        yes("...so the formula this replaced actually disagrees with the model here",
                Math.abs(theOldWay - theModel) > 1e-6);

        /* --------------------- and the assertions ---------------------
         *
         * WITHIN WHAT THE HISTORY ITSELF CAN HOLD, and no tighter. These are
         * not fudge factors, they are the file's storage precision written
         * down:
         *
         *   - recordMonth stores students and prisoners through round2(), so
         *     a labour force built from them is two cents of a person out at
         *     worst;
         *   - it stores the pool as a WHOLE person, and the model's own
         *     getUnemployed() already rounds, so filled posts reconstructed
         *     as (labour force - pool) can be half a person out. That is the
         *     only place this reconstruction loses anything, and half a
         *     person in a city is not a finding.
         *
         * Everything derived from those inherits the same slack and no more,
         * which is why each bound below is worked out from them rather than
         * picked. Asserting equality to 1e-9 here would be asserting that
         * round2() does not round.
         */
        near("the labour force is the model's labour force",
                YearBook.labourForce(h)[last], people.getLabourForce(), 2 * A_CENT);
        near("the filled posts are the model's filled posts",
                YearBook.filledPosts(h)[last], (double) people.getJobsFilled(),
                HALF_A_PERSON + 2 * A_CENT);
        near("unemployment is the model's own rate",
                YearBook.unemployment(h)[last], theModel,
                (HALF_A_PERSON + 2 * A_CENT) / people.getLabourForce());
        /*
          * The wage bill is asserted in two halves rather than one, because
          * the history stores money to the CENT and the live figure is not
          * rounded - comparing the book's average against the live average
          * fails on the fifth decimal for a reason that is not a bug. So:
          * the book divides the RECORDED bill by the right denominator, and
          * the recorded bill is the model's to the cent.
          */
        near("the average wage divides the recorded wage bill by the filled posts",
                YearBook.averageWage(h)[last],
                h.aligned("totalWage")[last] / people.getJobsFilled(),
                people.getTotalWage() / people.getJobsFilled()
                        * (HALF_A_PERSON + 2 * A_CENT) / people.getJobsFilled());
        near("...and the recorded wage bill is the model's, to the cent",
                h.aligned("totalWage")[last], people.getTotalWage(), A_CENT);
    }

    /** One template, n of them, paid for and standing - EducationCheck's shape. */
    private static void put(Game g, String name, int n) {
        for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
            if (b.getName().equals(name)) { g.buildStack(b, n, true); return; }
        }
        throw new IllegalStateException("no template " + name);
    }

    private static void near(String what, double got, double wanted, double slack) {
        checks++;
        if (Math.abs(got - wanted) > slack) {
            System.out.printf("  FAIL  %s: got %s, wanted %s (within %s)%n", what, got, wanted, slack);
            fails++;
        }
    }

    /** Twelve identical months of workforce, students, posts and (if not negative) the pool. */
    private static HistorySave history(int workforce, int students, int jobs, int outOfWork) {
        StringBuilder json = new StringBuilder("{\"month\":[");
        for (int m = 1; m <= 12; m++) json.append(m == 1 ? "" : ",").append(m);
        json.append("]");
        for (String[] s : new String[][] {{"workforce", "" + workforce}, {"students", "" + students},
                {"jobs", "" + jobs}, {"outOfWork", "" + outOfWork}}) {
            if (s[0].equals("outOfWork") && outOfWork < 0) continue;
            json.append(",\"").append(s[0]).append("\":[");
            for (int m = 1; m <= 12; m++) json.append(m == 1 ? "" : ",").append(s[1]);
            json.append("]");
        }
        json.append("}");
        return new Gson().fromJson(json.toString(), HistorySave.class);
    }

    /* ==================================================================
       12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE

       The rate is local dollars per US dollar (ForeignAccounts), so a bigger
       number is a weaker currency. The first edition's note said the
       opposite, and a reader who believed it would have read slot 3's
       founding collapse to 100 as a hundredfold appreciation.
       ================================================================== */
    private static void theCurrencyNoteReadsTheRightWay() {
        HistorySave h = built(12, "fxRate", ramp(12));
        String text = YearBook.years(h);
        yes("the fxRate note says Danzik dollars per US dollar", text.contains("Danzik dollars per US dollar"));
        yes("...and that higher is a fallen currency", text.contains("HIGHER means the currency has fallen"));
        yes("...and never the other way round", !text.contains("US dollars per Danzik dollar"));
    }

    /* ==================================================================
       14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE

       The Reports page marks them under its chart and the book lists them
       under WHAT HAPPENED, from one list - YearBook.episodes(). So the list is
       asserted on a hand-built history whose every episode is known:

       - the bank under water for fourteen months, months 11-24;
       - output growing a dollar a month and losing fourteen dollars of it
         for good at month 41. The rolling year is compared with the year
         before, and that comparison falls short only while eleven or twelve
         of its twelve months carry the loss: 144 of growth less 14 a month
         of loss is -10, -24, -10 for months 51, 52 and 53, and +4 either side.
         A recession of exactly three months, which is EPISODE_MIN_MONTHS;
       - a price index held at 1 and nothing else recorded, so nothing else
         can fire.

       Then the edges of the rules, each on its own history: a failure one
       month short of the minimum is nothing, a loss of thirteen (one month
       below the year before) is neither named nor shaded, two failures a
       year apart are two names, two in one year are "..., again", and two
       with less relief than EPISODE_JOIN_MONTHS between them are one.
       ================================================================== */
    private static void theEpisodesAreTheOnesTheFixtureCaused() {
        final int months = 96;
        final int lossAt = 40;                                  // index; month 41
        double[] equity = flat(months, 1000);
        for (int i = 10; i < 24; i++) equity[i] = -50;           // months 11-24, fourteen of them
        HistorySave h = built(months,
                new String[] {"bankEquity", "gdp", "priceIndex"},
                equity, steppedOutput(months, lossAt, 14), flat(months, 1));

        List<YearBook.Episode> got = YearBook.episodes(h);
        same("the fixture names two episodes and no others", got.size(), 2.0);
        if (got.size() == 2) {
            YearBook.Episode bank = got.get(0), slump = got.get(1);
            same("the first is the bank's", bank.kind(), "financial");
            same("...named for the year it began",
                    bank.name(), "Financial crisis of " + CityCalendar.yearOf(11));
            same("...from the month equity went under", bank.fromMonth(), 11.0);
            same("...to the last month it was under", bank.toMonth(), 24.0);
            same("...and its worst is the equity it reached", bank.worst(), -50.0);

            same("the second is the recession", slump.kind(), "recession");
            same("...named for the year it began",
                    slump.name(), "Recession of " + CityCalendar.yearOf(51));
            same("...from the month the year fell short of the one before", slump.fromMonth(), 51.0);
            same("...for exactly EPISODE_MIN_MONTHS months",
                    slump.toMonth() - slump.fromMonth() + 1, (double) YearBook.EPISODE_MIN_MONTHS);
            yes("...and its worst is a fall", slump.worst() < 0);
        }

        List<int[]> shaded = YearBook.recessions(h);
        same("the chart shades the same recession and nothing else", shaded.size(), 1.0);
        if (shaded.size() == 1) {
            same("...from the month it began", shaded.get(0)[0], 51.0);
            same("...to the month it ended", shaded.get(0)[1], 53.0);
        }

        String text = YearBook.years(h);
        yes("the book lists the crisis, one line with its months",
                text.contains("Financial crisis of " + CityCalendar.yearOf(11) + " - months 11-24"));
        yes("...and the recession",
                text.contains("Recession of " + CityCalendar.yearOf(51) + " - months 51-53"));

        /* ------------------------- the edges ------------------------- */
        double[] dip = flat(months, 1000);
        dip[30] = dip[31] = -50;
        same("a two-month dip under water is not a crisis",
                YearBook.episodes(built(months, "bankEquity", dip)).size(), 0.0);

        HistorySave blip = built(months, new String[] {"gdp", "priceIndex"},
                steppedOutput(months, lossAt, 13), flat(months, 1));
        same("a loss that leaves one month below the year before is not a recession",
                YearBook.episodes(blip).size(), 0.0);
        same("...and is not shaded", YearBook.recessions(blip).size(), 0.0);

        double[] twice = flat(months, 1000);
        for (int i = 5; i <= 7; i++) twice[i] = -50;             // months 6-8
        for (int i = 17; i <= 19; i++) twice[i] = -50;           // months 18-20, a year on
        List<YearBook.Episode> two = YearBook.episodes(built(months, "bankEquity", twice));
        same("two failures a year apart are two episodes", two.size(), 2.0);
        if (two.size() == 2) {
            yes("...with two names", !two.get(0).name().equals(two.get(1).name()));
            same("...the first for its year", two.get(0).name(), "Financial crisis of " + CityCalendar.yearOf(6));
            same("...the second for its own", two.get(1).name(), "Financial crisis of " + CityCalendar.yearOf(18));
        }

        double[] again = flat(months, 1000);
        for (int i = 0; i <= 2; i++) again[i] = -50;             // months 1-3
        for (int i = 3 + YearBook.EPISODE_JOIN_MONTHS; i <= 5 + YearBook.EPISODE_JOIN_MONTHS; i++) again[i] = -50;
        List<YearBook.Episode> inOneYear = YearBook.episodes(built(months, "bankEquity", again));
        same("two failures in one year, EPISODE_JOIN_MONTHS apart, are two episodes", inOneYear.size(), 2.0);
        if (inOneYear.size() == 2) {
            same("...and the second is the first's name, again",
                    inOneYear.get(1).name(), inOneYear.get(0).name() + ", again");
        }

        double[] joined = flat(months, 1000);
        for (int i = 0; i <= 2; i++) joined[i] = -50;            // months 1-3
        int back = 3 + YearBook.EPISODE_JOIN_MONTHS - 1;         // one month less relief
        for (int i = back; i <= back + 2; i++) joined[i] = -50;
        List<YearBook.Episode> one = YearBook.episodes(built(months, "bankEquity", joined));
        same("two failures with less relief than EPISODE_JOIN_MONTHS are one episode", one.size(), 1.0);
        if (one.size() == 1) {
            same("...from the first month of the first", one.get(0).fromMonth(), 1.0);
            same("...to the last month of the second", one.get(0).toMonth(), back + 3.0);
        }
    }

    /**
     * Output growing by one a month from 100, that loses `loss` a month for
     * good from index `at` on. Every figure is a whole number, so the file's
     * six decimals carry it exactly.
     */
    private static double[] steppedOutput(int months, int at, int loss) {
        double[] out = new double[months];
        for (int i = 0; i < months; i++) out[i] = 100 + i - (i >= at ? loss : 0);
        return out;
    }

    private static double[] flat(int months, double value) {
        double[] out = new double[months];
        java.util.Arrays.fill(out, value);
        return out;
    }

    /** A history of `months` months with several series filled in, each as long as the axis. */
    private static HistorySave built(int months, String[] series, double[]... values) {
        StringBuilder json = new StringBuilder("{\"month\":[");
        for (int m = 1; m <= months; m++) json.append(m == 1 ? "" : ",").append(m);
        json.append("]");
        for (int s = 0; s < series.length; s++) {
            json.append(",\"").append(series[s]).append("\":[");
            for (int i = 0; i < values[s].length; i++) {
                json.append(i == 0 ? "" : ",").append(String.format(Locale.ROOT, "%.6f", values[s][i]));
            }
            json.append("]");
        }
        json.append("}");
        return new Gson().fromJson(json.toString(), HistorySave.class);
    }

    /**
     * A history of `months` months with one series filled in.
     *
     * A series SHORTER than the month axis is how a real save records something
     * that began later, so a short array here lands at the END of the axis -
     * exactly as aligned() reads it off disk.
     */
    private static HistorySave built(int months, String series, double[] values) {
        StringBuilder json = new StringBuilder("{\"month\":[");
        for (int m = 1; m <= months; m++) json.append(m == 1 ? "" : ",").append(m);
        json.append("],\"").append(series).append("\":[");
        for (int i = 0; i < values.length; i++) {
            json.append(i == 0 ? "" : ",").append(String.format(Locale.ROOT, "%.6f", values[i]));
        }
        json.append("]}");
        return new Gson().fromJson(json.toString(), HistorySave.class);
    }

    private static double[] ramp(int n) {
        double[] out = new double[n];
        for (int i = 0; i < n; i++) out[i] = i + 1;
        return out;
    }

    private static double sum(int from, int to) {
        double s = 0;
        for (int i = from; i <= to; i++) s += i;
        return s;
    }

    /** The value a reader would take out of the file, found the way a reader finds it. */
    private static double cell(String text, String column, int row) {
        String raw = rawCell(text, column, row);
        return raw.isEmpty() ? Double.NaN : Double.parseDouble(raw);
    }

    private static boolean blank(String text, String column, int row) {
        return rawCell(text, column, row).isEmpty();
    }

    private static String rawCell(String text, String column, int row) {
        String[] lines = text.split("\n");
        int header = -1;
        for (int i = 0; i < lines.length; i++) {
            // the table headers are the only lines that start with yr/dec and carry tabs
            if ((lines[i].startsWith("yr\t") || lines[i].startsWith("dec\t")) && lines[i].contains(column)) {
                List<String> names = new ArrayList<>(List.of(lines[i].split("\t")));
                int col = names.indexOf(column);
                if (col < 0) continue;
                header = i;
                for (int j = header + 1; j < lines.length; j++) {
                    String[] parts = lines[j].split("\t", -1);
                    if (parts.length <= col) break;
                    if (Integer.parseInt(parts[0].trim()) == row) return parts[col].trim();
                }
            }
        }
        System.out.printf("  FAIL  no cell for column %s row %d%n", column, row);
        fails++;
        return "";
    }

    /* ----------------------------- assertions ----------------------------- */

    private static void same(String what, double got, double wanted) {
        checks++;
        if (Math.abs(got - wanted) > 1e-9) {
            System.out.printf("  FAIL  %s: got %s, wanted %s%n", what, got, wanted);
            fails++;
        }
    }

    private static void same(String what, String got, String wanted) {
        checks++;
        if (!got.equals(wanted)) {
            System.out.printf("  FAIL  %s: got \"%s\", wanted \"%s\"%n", what, got, wanted);
            fails++;
        }
    }

    private static void same(String what, boolean got, boolean wanted) {
        checks++;
        if (got != wanted) {
            System.out.printf("  FAIL  %s: got %s, wanted %s%n", what, got, wanted);
            fails++;
        }
    }

    private static void yes(String what, boolean got) {
        checks++;
        if (!got) {
            System.out.printf("  FAIL  %s%n", what);
            fails++;
        }
    }
}
