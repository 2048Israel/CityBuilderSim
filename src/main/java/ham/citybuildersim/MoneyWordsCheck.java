package ham.citybuildersim;

import ham.citybuildersim.ui.KeptBench;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

/**
 * The words of every page, read off the nodes, in a city whose money is written after the figure (FM1). Not part of the game.
 *
 * WHY THIS EXISTS. Jerus, 2026-11-02: the "$" was a literal in a hundred places, so a money with another sign ("2.6M kr") was
 * right on the screens that asked the money for its sign and wrong on every one that typed the character. MoneySignCheck holds
 * the writer; this holds the screens. It draws every page ui/KeptBench can draw - the rail's tabs and their pages, People, City
 * History, the header, the rail and the three panels - for a city founded with the Krona (KRO, "kr", after the figure), reads every
 * word off every node (a label's text and its graphic's, a text, a field's text and prompt, a tooltip, a tab, a choice) and says
 * which of them still hold a "$" that is not the mark of a foreign money. What a canvas draws is not a node and is not read.
 *
 * What it proves, a section each; every one a fixture that causes the condition:
 *
 *   1. The fixture: the same pages of a "$" city show "$", and the Krona's show " kr" - a page that draws nothing would pass
 *      section 2 without having been looked at.
 *   2. On the Krona's city no string holds a "$" except inside a foreign mark (US$, C$, A$: ForeignAccounts.MONEY_MARK, and
 *      Currency.FOREIGN_SYMBOL). Each string is printed once, with the page it was first read on, as one FAIL line.
 *
 * The city is the playtest's village founded by hand with the money (LongPlaytest.founding() and its first builds) and played
 * MONTHS months, no more: the pages need figures, not a history.
 */
public class MoneyWordsCheck {

    private static int fails, checks;

    static void check(String label, boolean ok) { check(label, ok, null); }

    static void check(String label, boolean ok, String detail) {
        checks++;
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    /** The months the two cities are played to before their pages are drawn: enough for a bank, a ledger and a trade, and for the pages to hold figures. */
    static final int MONTHS = 24;

    /** The fewest distinct strings holding a city's own sign that its pages must show for the fixture to count as drawn: a page that drew nothing would pass section 2 unread. */
    static final int AT_LEAST = 500;

    /** The most offending strings section 2 prints, each as a FAIL line; the rest are only counted. */
    static final int PRINTED = 60;

    /** How much of an offending string is printed round the "$": a window, so a long paragraph still says where. */
    static final int AROUND = 70;

    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get(10, TimeUnit.MINUTES);
    }

    /** The Krona: the money of section 2's city, written "2.6M kr". */
    static Currency krona() { return Currency.typed("Krona", "KRO", "kr", true); }

    /** The playtest's village founded with `money` (the founding's own when null), its first builds and months played, the game's narration off. */
    static Game city(String label, Currency money) {
        PrintStream realOut = System.out, savedReport = LongPlaytest.out;
        LongPlaytest.out = QUIET;
        System.setOut(QUIET);
        try {
            LongPlaytest.resetTheFloat();
            Founding f = LongPlaytest.founding();
            Game g = new Game(GameFiles.scratch(label), money == null ? f : f.withCurrency(money));
            g.run();
            LongPlaytest.childcareWhenNeeded(g);
            g.getCityMap();
            LongPlaytest.seedMarket(g, 0);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            LongPlaytest.run(g, 6);
            LongPlaytest.ensureSchools(g);
            LongPlaytest.probePlay(g, MONTHS);
            return g;
        } finally {
            LongPlaytest.out = savedReport;
            System.setOut(realOut);
        }
    }

    /* ------------------------------------------------------------ reading the words */

    /** Every word on a node and under it: a label's text and graphic, a text, a field's text and prompt, a tooltip, a tab, a choice. */
    static void words(Node n, Set<String> out, Map<Node, Boolean> seen) {
        if (n == null || seen.put(n, Boolean.TRUE) != null) return;
        if (KeptBench.tooltipOf(n) instanceof Tooltip tip) add(out, tip.getText());
        if (n instanceof Labeled l) {
            add(out, l.getText());
            words(l.getGraphic(), out, seen);
        }
        if (n instanceof Text t) add(out, t.getText());
        if (n instanceof TextInputControl c) {
            add(out, c.getText());
            add(out, c.getPromptText());
        }
        if (n instanceof Control c && c.getTooltip() != null) add(out, c.getTooltip().getText());
        if (n instanceof ComboBox<?> c) {
            add(out, c.getPromptText());
            for (Object o : c.getItems()) if (o != null) add(out, o.toString());
        }
        if (n instanceof ChoiceBox<?> c) for (Object o : c.getItems()) if (o != null) add(out, o.toString());
        if (n instanceof MenuButton m) for (MenuItem item : m.getItems()) add(out, item.getText());
        if (n instanceof TabPane tabs) {
            for (Tab tab : tabs.getTabs()) {
                add(out, tab.getText());
                words(tab.getContent(), out, seen);
            }
        }
        if (n instanceof ScrollPane s) words(s.getContent(), out, seen);
        if (n instanceof TitledPane p) words(p.getContent(), out, seen);
        if (n instanceof Parent p) for (Node k : new ArrayList<>(p.getChildrenUnmodifiable())) words(k, out, seen);
    }

    private static void add(Set<String> out, String s) {
        if (s != null && !s.isBlank()) out.add(s);
    }

    /** A page: the name the lines call it, and how it is drawn - the nodes it left on the bench. */
    record Named(String name, Callable<List<Node>> draw) { }

    /** One of KeptBench's pages, on the page it opens on and drawn from nothing kept: what `after` sets, then the nodes. */
    static Named page(KeptBench bench, String name, KeptBench.Page p, Runnable after) {
        return new Named(name, () -> {
            bench.home(p);
            if (after != null) after.run();
            bench.drawKept(p);
            return bench.pageTop();
        });
    }

    /** Every page KeptBench can draw, for the city it is built on. */
    static List<Named> pages(Game g, KeptBench bench) {
        List<Named> out = new ArrayList<>();
        out.add(new Named("People", () -> { bench.drawKept(); return bench.pageTop(); }));
        out.add(new Named("City History", () -> { bench.drawHistoryKept(); return bench.pageTop(); }));
        out.add(new Named("the header and the rail", () -> { bench.drawChromeKept(); return bench.chromeTops(); }));
        out.add(new Named("the city, construction and inbox panels", () -> { bench.drawPanelsKept(); return bench.panelTops(); }));
        for (String p : new String[]{"Roads", "Transit", "Freight", "Ports"}) {
            out.add(page(bench, "Infrastructure, " + p, bench.infrastructure(), () -> bench.infraOpen(p, "All")));
        }
        for (String[] ap : KeptBench.servicesPages()) {
            out.add(page(bench, "Services, " + ap[0] + ", " + ap[1], bench.services(), () -> bench.servicesOpen(ap[0], ap[1])));
        }
        out.add(page(bench, "the land office", bench.land(), null));
        for (String p : KeptBench.buildPages()) out.add(page(bench, "Build, " + p, bench.build("Build, " + p, p, Map.of()), null));
        for (String tab : new String[]{"Sites", "Timeline", "Demolish"}) out.add(page(bench, "Construction, " + tab, bench.construction(tab), null));
        out.add(page(bench, "the Bank Overview", bench.bank(), null));
        for (String p : KeptBench.bankPages()) {
            out.add(page(bench, "the Bank, " + p, bench.bank("the Bank, " + p, "pages", p, false), null));
            if (p.equals("Profit") || p.equals("Balance sheet")) {
                out.add(page(bench, "the Bank, " + p + " as a statement", bench.bank("the Bank, " + p + " as a statement", "pages", p, true), null));
            }
        }
        out.add(page(bench, "the Finances hub", bench.finances(), null));
        for (String area : KeptBench.financeAreas()) {
            for (String p : KeptBench.financePages(area)) {
                out.add(page(bench, "Finances, " + area + ", " + p, bench.finances("Finances, " + area + ", " + p, area, p), null));
            }
        }
        out.add(page(bench, "the Sectors list", bench.sectors(), null));
        List<String> keys = new ArrayList<>();
        keys.add(Sectors.RETAIL);
        for (Sector s : g.getSectors().all()) if (keys.size() < 3 && !keys.contains(s.key())) keys.add(s.key());
        for (String key : keys) {
            for (String p : KeptBench.sectorPages()) {
                out.add(page(bench, "Sectors, " + key + ", " + p, bench.sectors("Sectors, " + key + ", " + p, key, p, false), null));
            }
        }
        for (String p : KeptBench.tradePages()) out.add(page(bench, "Trade, " + p, bench.trade("Trade, " + p, p), null));
        for (String p : KeptBench.governmentPages()) out.add(page(bench, "Government, " + p, bench.government("Government, " + p, p), null));
        out.add(page(bench, "the Policy hub", bench.policy(), null));
        for (String[] ap : KeptBench.policyPages()) {
            out.add(page(bench, "Policy, " + ap[0] + ", " + ap[1], bench.policy("Policy, " + ap[0] + ", " + ap[1], ap[0], ap[1]), null));
        }
        return out;
    }

    /** What a city's pages say, page by page: each page once, the strings in the order they were read; a page that cannot be drawn is told on its own line. */
    static Map<String, Set<String>> read(Game g, String label) throws Exception {
        Map<String, Set<String>> said = new LinkedHashMap<>();
        fx(() -> { Currency.use(g.getCurrency()); return null; });
        KeptBench bench = fx(() -> new KeptBench(g));
        for (Named p : fx(() -> pages(g, bench))) {
            try {
                Set<String> words = fx(() -> {
                    Currency.use(g.getCurrency());
                    Set<String> out = new LinkedHashSet<>();
                    Map<Node, Boolean> seen = new IdentityHashMap<>();
                    for (Node n : p.draw().call()) words(n, out, seen);
                    return out;
                });
                said.put(p.name(), words);
            } catch (Exception e) {
                Throwable why = e.getCause() != null ? e.getCause() : e;
                check(label + ": " + p.name() + " can be drawn", false, why.toString());
            }
        }
        return said;
    }

    /** The distinct strings of every page. */
    static Set<String> distinct(Map<String, Set<String>> said) {
        Set<String> all = new LinkedHashSet<>();
        for (Set<String> s : said.values()) all.addAll(s);
        return all;
    }

    /** The foreign moneys' marks that hold a "$": the one a vault's figures are written with, and ForeignAccounts' others. */
    static List<String> foreignMarks() {
        List<String> marks = new ArrayList<>();
        marks.add(Currency.FOREIGN_SYMBOL);
        for (String m : ForeignAccounts.MONEY_MARK) if (m.contains("$") && !marks.contains(m.trim())) marks.add(m.trim());
        return marks;
    }

    /** Where in the string a "$" is that is not inside a foreign mark, or -1. */
    static int stray(String s, List<String> marks) {
        for (int at = s.indexOf('$'); at >= 0; at = s.indexOf('$', at + 1)) {
            boolean marked = false;
            for (String m : marks) {
                int k = m.indexOf('$');
                if (at - k >= 0 && s.startsWith(m, at - k)) marked = true;
            }
            if (!marked) return at;
        }
        return -1;
    }

    /** A string for a line: where the stray sign is, a window of it, its newlines shown. */
    static String showing(String s, int at) {
        int from = Math.max(0, at - AROUND), to = Math.min(s.length(), at + AROUND);
        return (from > 0 ? "..." : "") + s.substring(from, to).replace("\n", "\\n") + (to < s.length() ? "..." : "");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== MoneyWordsCheck: every word of every page, in a city whose money is written \"2.6M kr\" (FM1) ===");
        long started = System.currentTimeMillis();
        String down = KeptCheck.startJavaFx();
        if (down != null) {
            System.out.println("skipped: the JavaFX toolkit cannot start here (" + down + "); the pages need it to be drawn");
            System.exit(0);
        }
        try {
            run();
        } finally {
            Currency.use(Currency.DANZIK);
        }
        System.out.printf("%n%d assertions, %d failed, %.1f s%n", checks, fails, (System.currentTimeMillis() - started) / 1000.0);
        System.exit(Math.min(fails, 100));
    }

    static void run() throws Exception {
        Currency kr = krona();
        Game dollars = fx(() -> city("moneywordscheck-dollars", null));
        Game krona = fx(() -> city("moneywordscheck-krona", kr));
        System.out.printf("two cities played to month %d and %d: %s and %s%n", dollars.getMonth(), krona.getMonth(),
                dollars.getCurrency().describe(), krona.getCurrency().describe());

        Map<String, Set<String>> dollarPages = read(dollars, "the \"$\" city");
        Map<String, Set<String>> kronaPages = read(krona, "the Krona's city");

        /* ---------------------------------------------------------------- 1 */
        System.out.println("\n--- 1. the fixture: the same pages show the money's sign in each ---");
        Set<String> dollarStrings = distinct(dollarPages), kronaStrings = distinct(kronaPages);
        long dollarHolding = dollarStrings.stream().filter(s -> s.contains("$")).count();
        long kronaHolding = kronaStrings.stream().filter(s -> s.contains(" kr")).count();
        System.out.printf("  %d pages drawn for each; %,d distinct strings in the \"$\" city's, %,d in the Krona's%n",
                dollarPages.size(), dollarStrings.size(), kronaStrings.size());
        System.out.printf("  %,d strings of the \"$\" city's pages hold a \"$\"; %,d of the Krona's hold \" kr\"%n", dollarHolding, kronaHolding);
        check("the Krona's money is the one founded: written after the figure, \"kr\"", kr != null && krona.getCurrency().equals(kr) && kr.signAfter());
        check("both cities drew the same pages", dollarPages.keySet().equals(kronaPages.keySet()), dollarPages.size() + " and " + kronaPages.size());
        check("the \"$\" city's pages show \"$\" (at least " + AT_LEAST + " strings)", dollarHolding >= AT_LEAST, String.valueOf(dollarHolding));
        check("the Krona's pages show \" kr\" (at least " + AT_LEAST + " strings)", kronaHolding >= AT_LEAST, String.valueOf(kronaHolding));

        /* ---------------------------------------------------------------- 2 */
        System.out.println("\n--- 2. on the Krona's pages no string holds a \"$\" outside a foreign mark ---");
        List<String> marks = foreignMarks();
        System.out.println("  foreign marks: " + marks);
        Set<String> printed = new LinkedHashSet<>();
        int offending = 0;
        for (Map.Entry<String, Set<String>> page : kronaPages.entrySet()) {
            for (String s : page.getValue()) {
                int at = stray(s, marks);
                if (at < 0 || !printed.add(s)) continue;
                offending++;
                checks++;
                fails++;
                if (offending <= PRINTED) System.out.println("  FAIL " + page.getKey() + ": \"" + showing(s, at) + "\"");
            }
        }
        if (offending > PRINTED) System.out.println("  ... and " + (offending - PRINTED) + " more not printed");
        if (offending > 0) System.out.println("  " + offending + " strings of the Krona's pages hold a \"$\" that is not a foreign mark's");
        else check("no string of any page holds a \"$\" that is not a foreign mark's", true);
        fx(() -> { householdMoney(); return null; });
    }

    /** HS3: initial census/stocks, then native estates, fills and dividends; no displayed result is assigned. */
    static void householdMoney() throws Exception {
        System.out.println("\n--- 3. household money by stage, former tier, owners, estates and investment taxes (HS3) ---");
        PrintStream real = System.out;
        Game g;
        System.setOut(QUIET);
        try {
            g = new Game(GameFiles.scratch("hs3-money"), LongPlaytest.founding());
            g.run();
        } finally { System.setOut(real); }
        Currency.use(g.getCurrency());
        HouseholdBalance bal = g.getHouseholdBalance();
        FamilyModel families = g.getFamilies();
        double[] jobs = new double[PayTier.values().length];
        jobs[PayTier.COLLEGE.ordinal()] = 500;
        jobs[PayTier.ELITE.ordinal()] = 500;
        families.rebuild(HouseholdMemoryCheck.staged(0, 0, 0, 1000, 400), jobs);
        for (Household c : bal.cells()) if (c instanceof WorkingHousehold w) {
            c.households = families.get(c.shape(), c.tier(), w.stage());
            c.savings = w.stage() == AdultStage.YOUNG ? 333 : 777;
        }
        Household college = bal.cell(FamilyStructure.ELDER_ALONE, PayTier.COLLEGE);
        Household elite = bal.cell(FamilyStructure.ELDER_ALONE, PayTier.ELITE);
        college.households = 20; college.savings = 1000; college.debt = 200;
        elite.households = 20; elite.savings = 50; elite.debt = 200;
        double[] mix = families.toSaveArray();
        mix[FamilyStructure.ELDER_ALONE.ordinal() * PayTier.values().length] = 32;
        families.restore(PopulationCohorts.saveBands(), FamilyModel.saveShapes(), mix);
        bal.setOwnerLine(true);
        bal.restoreOwnerLine(Map.of(Equity.COMPANIES[0], 0));
        bal.owners().households = 1;
        bal.setOutsideCensus(Household::households);
        bal.setFlows(0, 0, 0, 0, .2, 0, 0);
        bal.censusOnly(families);
        check("two former tiers survive the estate month", college.households() >= .5 && elite.households() >= .5);
        check("dying alone caused estate cash, repayment and write-off", bal.getFlowHouseholds(HouseholdBalance.DYING_ALONE) > 0
                && bal.getEstatePassed() > 0 && bal.getEstateRepaid() > 0 && bal.getEstateWrittenOff() > 0);

        // Use the same real book/fill path as CapitalGainsCheck, with this city's balance.
        Equity equity = ExchangeCheck.equity();
        Exchange exchange = ExchangeCheck.exchange();
        Bank bank = ExchangeCheck.bankWith(0);
        equity.listIfUnlisted(0, 10_000, bal);
        ExchangeCheck.addPaid(equity, 0, 100, 10);
        ExchangeCheck.Firms firms = new ExchangeCheck.Firms();
        firms.wage = 1;
        System.setOut(QUIET);
        try { ExchangeCheck.step(exchange, equity, bal, bank, firms, ExchangeCheck.bookOf(0, 10_000),
                DebtManager.WORLD_BASE_RATE, 0, 1); }
        finally { System.setOut(real); }
        equity.moveForeign(0, bal.sharesHeld(0));
        for (Household c : bal.cells()) { c.shares[0] = 0; c.basis[0] = 0; }
        Household holder = bal.cell(FamilyStructure.SINGLE_ADULT, PayTier.ELITE);
        check("the census supplied an investor at the elite tier", holder.households() >= .5);
        holder.savings = 20_000;
        TaxPolicy policy = g.getEconomyManager().getTaxPolicy();
        policy.setWageTaxRate(.30);
        bal.setInvestmentTaxes(policy, exchange::price, (line, amount) -> { });
        bal.attachInvestmentRegister(equity, exchange::shortShares);
        String cell = Exchange.CELL + holder.key();
        exchange.bookOf(0).withdrawAll();
        exchange.tradeForCheck(0, Exchange.WORLD, OrderBook.Side.SELL, 2, 100);
        check("the investor actually buys the lot", FundLedgerCheck.filled(exchange.tradeForCheck(0, cell, OrderBook.Side.BUY, 2, 100)) == 100);
        exchange.bookOf(0).withdrawAll();
        exchange.tradeForCheck(0, Exchange.WORLD, OrderBook.Side.BUY, 4, 50);
        check("the investor actually sells at a gain", FundLedgerCheck.filled(exchange.tradeForCheck(0, cell, OrderBook.Side.SELL, 4, 50)) == 50);
        bal.creditDividend(0, 1);
        check("the fills and dividend cause both tax lines", bal.getCapitalGainsTax() > 0 && bal.getDividendTax() > 0);

        KeptBench bench = new KeptBench(g);
        KeptBench.Page page = bench.householdMoney();
        bench.home(page);
        bench.householdStatement(true);
        bench.drawKept(page);
        bench.passes();
        Set<String> words = pageWords(bench);
        for (AdultStage stage : AdultStage.values()) {
            check("the page names " + stage.getLabel(), words.contains(stage.getLabel() + " · " + stage.getAges()));
            String key = PayTier.ELITE.ordinal() + ":" + FamilyStructure.SINGLE_ADULT + stage.keySuffix();
            check("the stage has its own clickable cell: " + key, bench.householdCells().contains(key));
            bench.householdCell(key);
            bench.drawKept(page);
            check("opening the stage shows its own ledger", pageWords(bench).contains(FamilyStructure.SINGLE_ADULT.getLabel()
                    + "  ·  " + PayTier.ELITE.getLabel() + "  ·  " + stage.getLabel() + ", " + stage.getAges()));
            check("the stage's statement count is its model census", g.getHouseholds().statementFor(families,
                    FamilyStructure.SINGLE_ADULT, PayTier.ELITE, stage).households() == families.get(FamilyStructure.SINGLE_ADULT, PayTier.ELITE, stage));
            check("the stage's open ledger shows its own savings", lineShows(bench, "Put by",
                    ham.citybuildersim.ui.Money.tightMoney(bal.cell(FamilyStructure.SINGLE_ADULT, PayTier.ELITE, stage).savings() * 1000, false)));
        }
        check("both former tiers are named on the page", words.contains(college.label()) && words.contains(elite.label()));
        check("the owner family has its own line", words.contains(bal.owners().label()) && bench.householdCells().contains("OUT:" + bal.owners().key()));
        String[] labels = {"Estate cash passed to heirs", "Estate debt repaid", "Estate debt written off", "Tax on realised gains", "Tax on dividends"};
        double[] amounts = {bal.getEstatePassed(), bal.getEstateRepaid(), bal.getEstateWrittenOff(), bal.getCapitalGainsTax(), bal.getDividendTax()};
        for (int i = 0; i < labels.length; i++) {
            String amount = ham.citybuildersim.ui.Money.tightMoney(amounts[i] * 1000, false);
            check("the page shows the model's " + labels[i], lineShows(bench, labels[i], amount), amount);
        }
        bench.passes();
        bench.settle();
        List<String> kept = bench.signature();
        bench.showFresh(page);
        bench.settle();
        if (!kept.equals(bench.signature())) {
            java.nio.file.Files.write(java.nio.file.Path.of("target/HS3-kept.txt"), kept);
            java.nio.file.Files.write(java.nio.file.Path.of("target/HS3-fresh.txt"), bench.signature());
        }
        check("the stage page kept equals the page built new", kept.equals(bench.signature()));
        bench.restoreKeptPage();
        ScrollPane scroll = bench.householdScroller();
        scroll.setVvalue(.43);
        Set<Node> nodes = bench.nodesOnPage();
        // A second census consumes the next month's zero death input and resets that month's estate disclosures.
        bal.setFlows(0, 0, 0, 0, 0, 0, 0);
        bal.censusOnly(families);
        bench.showHousehold();
        bench.passes();
        check("the real redraw keeps the same scroller and its position", scroll == bench.householdScroller() && scroll.getVvalue() == .43);
        check("the redraw updates the estate line to the new month's model", bal.getEstatePassed() == 0
                && lineShows(bench, labels[0], ham.citybuildersim.ui.Money.tightMoney(0, false)));
        check("the redraw retains the month's disclosure nodes", bench.nodesOnPage().containsAll(nodes));
        bench.release();
    }

    static Set<String> pageWords(KeptBench bench) {
        Set<String> out = new LinkedHashSet<>();
        for (Node n : bench.pageTop()) words(n, out, new IdentityHashMap<>());
        return out;
    }

    static boolean lineShows(KeptBench bench, String label, String amount) {
        for (Node n : bench.nodesOnPage()) if (n instanceof Labeled l && label.equals(l.getText())) {
            Set<String> row = new LinkedHashSet<>();
            words(n.getParent(), row, new IdentityHashMap<>());
            if (row.contains(amount)) return true;
        }
        return false;
    }
}
