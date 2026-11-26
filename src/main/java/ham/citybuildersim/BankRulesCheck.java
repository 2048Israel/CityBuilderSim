package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The window against collateral and the bank's rules as the player's dials.
 * WHY (CBK3): a capital-only equity desk could be financed without limit at
 * the window. Numbered sections cause collateral, funding and credit limits;
 * the optional evidence run plays a second copy of the supplied autosave.
 */
public final class BankRulesCheck {
    static int fails;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-76s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void silently(Runnable action) {
        PrintStream before = System.out;
        System.setOut(quiet);
        try { action.run(); } finally { System.setOut(before); }
    }

    /** Reads only the supplied evidence folder; all play and autosaves use a fresh scratch. */
    static void evidence(Path source) throws Exception {
        out.println("--- 6. the copied month-133 city played forward ---");
        Path root = TempDirs.create("cbk3-evidence");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        for (String name : new String[]{"autosave.json", "autosave-history.json", "autosave-map.bin"})
            Files.copy(source.resolve(name), files.savesDirectory().resolve(name));
        Game g = new Game(files);
        silently(() -> g.loadGameSave(GameFiles.AUTOSAVE_SLOT));
        check("the evidence loaded month 133 on scratch", g.getMonth() == 133);
        Bank bank = g.getBank();
        CentralBank cb = g.getCentralBank();
        out.printf("EVIDENCE start month=%d desk=%.9f cash=%.9f window=%.9f revenue12=%.9f failures=%d%n",
                g.getMonth(), bank.getSecurities(), bank.getCash(), cb.getAdvancesToBank(),
                cb.trailingRevenue() * 12, bank.getFailures());
        int failures = bank.getFailures();
        double revenue = 0, remitted = 0, worstAudit = 0;
        int months = Integer.getInteger("cbs.cbk3.months", 120);
        for (int i = 0; i < months; i++) {
            int before = g.getMonth();
            silently(g::toggleNextMonth);
            check("evidence month advances " + before, g.getMonth() == before + 1);
            revenue += g.getEconomyManager().getNationalAccounts().getTotalRevenue();
            remitted += cb.getRemitted();
            MoneyAudit.Result audit = g.getLastMoneyAudit();
            check("evidence money audit at month " + g.getMonth(), audit != null
                    && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved()));
            if (audit != null) worstAudit = Math.max(worstAudit, Math.abs(audit.residual));
            if ((i + 1) % 12 == 0)
                out.printf("EVIDENCE month=%d share=%.9f desk=%.9f cash=%.9f window=%.9f failures=%d audit=%.12g%n",
                        g.getMonth(), revenue > 0 ? remitted / revenue : 0,
                        bank.getSecurities(), bank.getCash(), cb.getAdvancesToBank(),
                        bank.getFailures(), worstAudit);
        }
        out.printf("EVIDENCE final months=%d remittance=%.9f revenue=%.9f share=%.9f newFailures=%d audit=%.12g%n",
                months, remitted, revenue, revenue > 0 ? remitted / revenue : 0,
                bank.getFailures() - failures, worstAudit);
        g.awaitSaves();
    }

    public static void main(String[] args) throws Exception {
        String source = System.getProperty("cbs.cbk3.evidence", "");
        if (!source.isBlank()) evidence(Path.of(source));
        out.printf("BankRulesCheck: %d failures%n", fails);
        System.exit(fails == 0 ? 0 : 1);
    }
}
