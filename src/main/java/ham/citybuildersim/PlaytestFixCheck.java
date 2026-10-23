package ham.citybuildersim;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Verifies the small fixes of a played city (PT1): words and the log, nothing
 * the month does. Not part of the game.
 *
 * WHY THIS EXISTS
 *
 * Jerus played a city and wrote down what read wrongly: a bank "that is none"
 * while one stood, a power plant "13333% served", a notice toasted again on a
 * load, two lines of the log. Each is a sentence, so none of them changes a
 * figure in the model, and a sentence that is wrong is only found by someone
 * reading it. Each is held here by a fixture that causes it and asserts
 * against the model's own constant.
 *
 * What this has to prove, by section (the population chart's axis, PT1 item
 * 6, is ChartCheck's section 9):
 *
 *   1. A BANK THAT STANDS BEFORE IT HAS OPENED IS NOT "NONE". A founded
 *      city has its Commercial Bank standing, and Bank.getBranches() is 0
 *      until the first month runs: NEEDS YOU reads "opens with the first
 *      month" and does not count the row; the same city with no bank
 *      standing still counts it, red, "there is none"; and once a month has
 *      run the bank is open.
 *   2. SERVED PAST A FEW TIMES WHAT IS ASKED PRINTS AS A MULTIPLE. Just
 *      under CityNeeds.SERVED_AS_MULTIPLE it is a per cent; from it up a
 *      multiple, "30×"; a share no one asks of is still a dash.
 *   3. A NOTICE RAISED BEFORE A SAVE DOES NOT TOAST ON LOAD. A loaded inbox
 *      holding an urgent unread notice, with the toast set emptied as
 *      anotherCity() empties it, would toast it at once; after
 *      Inbox.markAllToasted() it does not, it stays unread and urgent, and
 *      a notice raised afterwards toasts once and not twice.
 *   4. TWO LINES OF THE LOG. "Debt-to-GDP Ratio" in a month with no GDP
 *      prints a dash, not NaN; "Interest Rate" has its per cent sign after
 *      the figure.
 *
 * Every fixture causes its condition.
 */
public class PlaytestFixCheck {

    static int fails = 0;

    static final PrintStream OUT = System.out;
    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
    });

    static void quietly(Runnable work) {
        System.setOut(QUIET);
        try { work.run(); } finally { System.setOut(OUT); }
    }

    /** What the work printed, as text. */
    static String printed(Runnable work) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
        try { work.run(); } finally { System.setOut(OUT); }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    /** A figure for a label: the multiple sign and the dash as plain characters, so a console that is not UTF-8 reads it. */
    static String plain(String text) {
        return text.replace("×", "x").replace("—", "-");
    }

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        OUT.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static CityNeeds.Need bankRow(Game g) {
        for (CityNeeds.Need n : CityNeeds.measure(g, CityNeeds.PLAIN)) if (n.kind() == CityNeeds.Kind.BANK) return n;
        throw new IllegalStateException("NEEDS YOU measures no bank row");
    }

    static boolean bankIsListed(Game g) {
        for (CityNeeds.Need n : CityNeeds.needsYou(g)) if (n.kind() == CityNeeds.Kind.BANK) return true;
        return false;
    }

    public static void main(String[] args) throws Exception {
        Path root = TempDirs.create("playtestfix");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ============ 1. a bank that stands before it has opened ============ */
        OUT.println("--- 1. a Commercial Bank standing before the first month is not \"none\" ---");

        Game city = new Game(files);
        quietly(city::newGame);

        int standing = city.getBankBranchesStanding();
        check("premise: a founded city has a Commercial Bank standing, and the bank has opened no branch",
                standing >= 1 && city.getMonth() <= 1 && city.getBank().getBranches() == 0);
        CityNeeds.Need unopened = bankRow(city);
        check("NEEDS YOU's bank row reads that it opens with the first month, not that there is none",
                unopened.reading().equals("opens with the first month"));
        check("...and the row is not counted: no level, and not among NEEDS YOU's rows",
                unopened.level() == 0 && !bankIsListed(city));

        BuildingsTemplate branch = template(city, "Commercial Bank");
        int scrapped = city.getBuildingManager().retire(branch, standing);
        check("premise for a city with no bank: the standing branches are scrapped, none open, none standing",
                scrapped == standing && city.getBankBranchesStanding() == 0 && city.getBank().getBranches() == 0);
        CityNeeds.Need none = bankRow(city);
        check("a city with no bank standing keeps today's words: \"there is none\"", none.reading().equals("there is none"));
        check("...red, and listed in NEEDS YOU", none.level() == 2 && bankIsListed(city));

        Game opened = new Game(files);
        quietly(opened::newGame);
        quietly(() -> opened.simulateMonths(1));
        check("once the first month has run, the bank has opened its branches and the row says neither",
                opened.getBank().getBranches() >= 1 && !bankRow(opened).reading().equals("opens with the first month")
                        && !bankRow(opened).reading().equals("there is none") && !bankIsListed(opened));

        /* ============ 2. served as a multiple ============ */
        OUT.println("\n--- 2. a served share from the dial up prints as a multiple, below it as a per cent ---");

        double dial = CityNeeds.SERVED_AS_MULTIPLE;
        String under = CityNeeds.servedPct(dial - .01), at = CityNeeds.servedPct(dial);
        check("just under the dial (" + (dial - .01) + ") it is a per cent: " + under,
                under.equals(String.format("%.0f%%", (dial - .01) * 100)) && under.endsWith("%") && !under.contains("×"));
        check("at the dial (" + dial + ") it is a multiple: " + plain(at),
                at.equals(String.format("%.0f×", dial)) && !at.contains("%"));
        double town = CityNeeds.servedShare(4_000, 30);                 // a town of 40: 133 times what it asks
        String power = CityNeeds.servedPct(town);
        check("the brief's town, a plant of 4,000 for a demand of 30, reads " + plain(power) + " and not 13333%",
                town > dial && power.equals(String.format("%.0f×", town)) && !power.contains("13333"));
        check("a share well under the dial is as it was, \"56%\" and \"100%\"; just under 100% is still 99%",
                CityNeeds.servedPct(.56).equals("56%") && CityNeeds.servedPct(1).equals("100%")
                        && CityNeeds.servedPct(.9996).equals("99%"));
        check("...and a share nobody asks of is still a dash",
                CityNeeds.servedPct(Double.POSITIVE_INFINITY).equals("—") && CityNeeds.servedPct(Double.NaN).equals("—"));

        /* ============ 3. a notice does not toast again on load ============ */
        OUT.println("\n--- 3. a notice raised before the save is not toasted again by the load ---");

        Inbox saved = new Inbox();
        Notice before = new Notice("landlock", "Your businesses have nowhere to build", List.of("No ground."), 12);
        saved.restoreFrom(List.of(before));                              // what a load does with the save's inbox
        Set<String> toasted = new HashSet<>();                           // anotherCity() has just cleared it
        check("premise: the loaded inbox holds one unread notice that is still true, and the envelope is red",
                saved.size() == 1 && saved.urgent() == before && saved.unread() == 1);
        Inbox unmarked = new Inbox();
        unmarked.restoreFrom(List.of(before));
        check("without the load's marking, an emptied set would toast it at once (the fault)",
                unmarked.toToast(new HashSet<>()).size() == 1);

        saved.markAllToasted(toasted);
        check("after markAllToasted(), nothing is to be toasted", saved.toToast(toasted).isEmpty());
        check("...the notice is still unread and urgent, and the envelope is still red",
                saved.urgent() == before && !before.isRead() && saved.unread() == 1);
        check("...its key is the key and the month it was raised: " + Inbox.toastKey(before),
                Inbox.toastKey(before).equals("landlock@12") && toasted.contains("landlock@12"));

        Notice after = new Notice("shedding", "Your builders are being laid off", List.of("Shed."), 13);
        saved.all().add(after);                                          // raised in the first month after the load
        List<Notice> first = saved.toToast(toasted);
        check("a notice raised after the load is toasted once", first.size() == 1 && first.get(0) == after);
        check("...and not twice, the next time the window asks", saved.toToast(toasted).isEmpty());

        Notice read = new Notice("crime", "Crime is running above Canada's", List.of("Crime."), 14);
        saved.all().add(read);
        saved.markRead(read, 14);
        check("a notice already read is never toasted", saved.toToast(toasted).isEmpty());

        Notice older = new Notice("bank", "The bank has failed", List.of("Failed."), 15);
        Notice newer = new Notice("resolved", "The city resolved the bank", List.of("Done."), 16);
        saved.all().add(older);
        saved.all().add(newer);
        List<Notice> two = saved.toToast(toasted);
        check("two raised together come oldest first, the newest last, for the corner it lands in",
                two.size() == 2 && two.get(0) == older && two.get(1) == newer);

        /* ============ 4. two lines of the log ============ */
        OUT.println("\n--- 4. the log: a dash before there is any GDP, and the per cent sign after the rate ---");

        String noGdp = EconomyManager.debtToGdpLine(0, 0);
        String infinite = EconomyManager.debtToGdpLine(5_000, 0);
        String normal = EconomyManager.debtToGdpLine(50_000, 200_000);
        check("with no GDP and no debt the line has a dash and no NaN: " + plain(noGdp.trim()),
                !noGdp.contains("NaN") && noGdp.contains("—") && !noGdp.contains("%"));
        check("with debt and no GDP it has a dash and no Infinity: " + plain(infinite.trim()),
                !infinite.contains("Infinity") && infinite.contains("—"));
        check("with a GDP it prints the per cent: " + normal.trim(),
                normal.contains("25.00%") && normal.startsWith("Debt-to-GDP Ratio:"));

        Game young = new Game(files);
        quietly(young::newGame);
        check("premise: a founded city has no GDP before its first month",
                young.getEconomyManager().getYearGdp() == 0);
        String log = printed(young::toggleNextMonth);
        String debtLine = lineStarting(log, "Debt-to-GDP Ratio:");
        String rateLine = lineStarting(log, "Interest Rate:");
        check("month 1's log prints a debt-to-GDP line, and it is not NaN: " + plain(String.valueOf(debtLine)),
                debtLine != null && !debtLine.contains("NaN"));
        check("month 1's log prints the interest rate with its sign after the figure: " + rateLine,
                rateLine != null && rateLine.matches("Interest Rate: -?[0-9][0-9,.]*%") && !rateLine.contains("%3"));

        OUT.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The first line of `log` that starts with `prefix`, trimmed, or null. */
    static String lineStarting(String log, String prefix) {
        for (String line : log.split("\\R")) if (line.startsWith(prefix)) return line.trim();
        return null;
    }
}
