package ham.citybuildersim;

import ham.citybuildersim.ui.RenderXR2Page;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Scratch, not in the build (like RenderCL4): XR2's screens as PNGs. Plays a scratch city (GameFiles.scratch(), nothing of Jerus's
 * is read or written) on CurrencyRegimeCheck's trading town, 36 months under Lean and then under each regime - a band and a peg
 * with a vault deepened by some months of imports, played to the last month the hold held and, for a peg, to the month it broke -
 * and writes Policy › Money › The currency for each, with a choice staged on each so the Apply foot shows; the policy rate page
 * under the peg; and Trade's rate card and forces under each. The JavaFX half is ui.RenderXR2Page, because the screens are
 * package-private.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/XR2/RenderXR2Page.java docs/queue/XR2/RenderXR2.java
 *   java -cp "<scratch>;target/classes;<classpath.txt>" ham.citybuildersim.RenderXR2 docs/queue/XR2
 */
public class RenderXR2 {
    static final ForeignAccounts.Regime LEAN = ForeignAccounts.Regime.LEAN, BAND = ForeignAccounts.Regime.BAND,
            PEG = ForeignAccounts.Regime.PEG;

    public static void main(String[] args) throws Exception {
        File dir = new File(args.length > 0 ? args[0] : "docs/queue/XR2");
        CurrencyRegimeCheck.quiet = new PrintStream(OutputStream.nullOutputStream());
        javafx.application.Platform.startup(() -> { });

        // Lean: the default. Stage a band, so the "entering" sentence and the Apply foot show.
        Game lean = city("xr2-render-lean", 24, LEAN, 0, null);
        Map<String, Double> bandChosen = new LinkedHashMap<>();
        bandChosen.put("xr:regime", (double) BAND.ordinal());
        RenderXR2Page.snapshot(lean, "currency", bandChosen, 1180, new File(dir, "currency-lean.png"));
        RenderXR2Page.snapshot(lean, "trade", Map.of(), 900, new File(dir, "trade-lean.png"));

        // A band, in the last month its vault sold dollars to hold the weak edge (else the month the run ends): stage a wider width and the mix aim.
        Game band = city("xr2-render-band", 40, BAND, 8, g -> {
            ForeignAccounts fx = g.getForeignAccounts();
            return fx.getDefenceUsd() > 0 && fx.isHeld() && fx.getRate() <= fx.bandWeakEdge() * (1 + 1e-9);
        });
        Map<String, Double> bandSteps = new LinkedHashMap<>();
        bandSteps.put("xr:width", band.getForeignAccounts().getBandWidth() + .01);
        bandSteps.put("xr:aim", (double) DebtManager.Aim.MIX.ordinal());
        RenderXR2Page.snapshot(band, "currency", bandSteps, 1180, new File(dir, "currency-band.png"));
        RenderXR2Page.snapshot(band, "trade", Map.of(), 900, new File(dir, "trade-band.png"));

        // A peg, in a month it held and the vault paid: stage a devaluation of two steps and a wider spread.
        Game peg = city("xr2-render-peg", 40, PEG, 16, g -> {
            ForeignAccounts fx = g.getForeignAccounts();
            return fx.getRegime() == PEG && fx.getDefenceUsd() > 0 && g.getMonth() >= 36 + 5;
        });
        Map<String, Double> pegSteps = new LinkedHashMap<>();
        pegSteps.put("xr:peg", peg.getForeignAccounts().getPegRate() * 1.02);
        pegSteps.put("xr:spread", peg.getDebtManager().getPegSpread() + .005);
        RenderXR2Page.snapshot(peg, "currency", pegSteps, 1180, new File(dir, "currency-peg.png"));
        RenderXR2Page.snapshot(peg, "rate", Map.of(), 1180, new File(dir, "rate-peg.png"));
        RenderXR2Page.snapshot(peg, "trade", Map.of(), 900, new File(dir, "trade-peg.png"));
        // ...and the same peg with the autopilot aiming at the currency: the rule sets the spread, so the steps say so.
        peg.getDebtManager().setAutopilot(true);
        peg.getDebtManager().setAim(DebtManager.Aim.CURRENCY);
        RenderXR2Page.snapshot(peg, "currency", Map.of(), 1180, new File(dir, "currency-peg-rule.png"));

        // The month a peg broke, and Trade's forces in it (the hold is the peg letting go); and the page a few months on.
        Game broke = city("xr2-render-break", 40, PEG, 16, g -> g.getForeignAccounts().brokeThisMonth());
        RenderXR2Page.snapshot(broke, "currency", Map.of(), 1180, new File(dir, "currency-break.png"));
        RenderXR2Page.snapshot(broke, "trade", Map.of(), 900, new File(dir, "trade-break.png"));
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /**
     * A trading town, 36 months under Lean, then under the regime (a peg or a band struck at the rate of the day) with its vault
     * deepened by `vaultMonths` months of imports first. It plays up to `months` months; with a `stop` it plays them one at a time and
     * stops at the first month `stop` says yes to - else the last (and says so).
     */
    static Game city(String label, int months, ForeignAccounts.Regime regime, double vaultMonths, Predicate<Game> stop) {
        Game g = CurrencyRegimeCheck.town(label, Founding.defaults());
        CurrencyRegimeCheck.play(g, 36);
        ForeignAccounts fx = g.getForeignAccounts();
        if (regime != LEAN) CurrencyRegimeCheck.quietly(() -> g.setCurrencyRegime(regime));
        if (vaultMonths > 0) {
            fx.buyReserves(vaultMonths * fx.monthlyImports());
            fx.forgetDealing();
        }
        boolean stopped = false;
        for (int m = 0; m < months && !stopped; m++) {
            CurrencyRegimeCheck.play(g, 1);
            stopped = stop != null && stop.test(g);
        }
        System.out.printf("%s: month %d%s, regime %s, rate %.4f, shadow %.4f, vault US$%,.0fk, cover %.1f, breaks %d, defence US$%,.0fk, edge bought US$%,.0fk%n",
                label, g.getMonth(), stop == null ? "" : stopped ? " (stopped where asked)" : " (NEVER stopped: the last month)", fx.getRegime(),
                fx.getRate(), fx.getShadowRate(), fx.getReservesUsd(), fx.importCover(), fx.getBreaks(), fx.getDefenceUsd(), fx.getEdgeBoughtUsd());
        return g;
    }
}
