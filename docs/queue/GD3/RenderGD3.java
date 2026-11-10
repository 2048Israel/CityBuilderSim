package ham.citybuildersim;

import ham.citybuildersim.ui.RenderGD3Page;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Scratch, not in the build (like RenderXR2): GD3's screens as PNGs. Plays a scratch city (GameFiles.scratch(), nothing of Jerus's
 * is read or written) on CurrencyRegimeCheck's trading town, 36 months under Lean, then buys gold into the vault (a share of M0, at
 * the world's gold price) and puts the D$ on gold; and writes Policy > Money > The currency for:
 *
 *   currency-gold.png        a town on gold, the cover over the law's 40%, three months played;
 *   currency-gold-narrow.png ...the same at 520 wide;
 *   currency-gold-thin.png   the cover under the law's 40% (gold bought for a quarter of M0), the first month's page;
 *   currency-gold-1933.png   the April 1933 decision open (ForeignAccounts.openGoldEvent(), a bare state through the model's own call);
 *   currency-gold-staged.png Lean with gold staged: the par to strike two steps up, the vault holding none (the warning), the Apply foot.
 *
 * Needs the world file (gold has a price only with it): run with the properties the build takes. The JavaFX half is ui.RenderGD3Page,
 * because the screens are package-private.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/GD3/RenderGD3Page.java docs/queue/GD3/RenderGD3.java
 *   java -Dcbs.earth=C:/.../data/earth-2.bin -Dcbs.world=C:/.../data/world-1.bin -cp "<scratch>;target/classes;<classpath.txt>" ham.citybuildersim.RenderGD3 docs/queue/GD3
 */
public class RenderGD3 {
    static final ForeignAccounts.Regime GOLD = ForeignAccounts.Regime.GOLD;

    public static void main(String[] args) throws Exception {
        File dir = new File(args.length > 0 ? args[0] : "docs/queue/GD3");
        CurrencyRegimeCheck.quiet = new PrintStream(OutputStream.nullOutputStream());
        javafx.application.Platform.startup(() -> { });

        // On gold, the cover well over the law's, three months played.
        Game gold = city("gd3-render-gold", .65, 3);
        RenderGD3Page.snapshot(gold, "currency", Map.of(), 1180, new File(dir, "currency-gold.png"));
        RenderGD3Page.snapshot(gold, "currency", Map.of(), 520, new File(dir, "currency-gold-narrow.png"));

        // On gold, the cover under the law's: the page of the month it was struck.
        Game thin = city("gd3-render-thin", .25, 0);
        RenderGD3Page.snapshot(thin, "currency", Map.of(), 1180, new File(dir, "currency-gold-thin.png"));

        // The US$ leaves gold (April 1933): the decision open on the card.
        Game left = city("gd3-render-1933", .65, 0);
        left.getForeignAccounts().openGoldEvent(ForeignAccounts.GoldEvent.LEFT_1933);
        RenderGD3Page.snapshot(left, "currency", Map.of(), 1180, new File(dir, "currency-gold-1933.png"));

        // Lean, with gold staged and a par two steps above the day's.
        Game lean = CurrencyRegimeCheck.town("gd3-render-staged", Founding.defaults());
        CurrencyRegimeCheck.play(lean, 36);
        ForeignAccounts fx = lean.getForeignAccounts();
        double day = fx.getRate() * fx.getMoneyUsd(ForeignAccounts.GOLD);
        Map<String, Double> staged = new LinkedHashMap<>();
        staged.put("xr:regime", (double) GOLD.ordinal());
        staged.put("xr:mint", day * 1.02);
        RenderGD3Page.snapshot(lean, "currency", staged, 1180, new File(dir, "currency-gold-staged.png"));
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /**
     * A trading town, 36 months under Lean, then gold bought into the vault for `coverShare` of M0 (local money, at the day's rate) and the D$
     * put on gold; it then plays up to `months` months, stopping early if the gold gave way.
     */
    static Game city(String label, double coverShare, int months) {
        Game g = CurrencyRegimeCheck.town(label, Founding.defaults());
        CurrencyRegimeCheck.play(g, 36);
        ForeignAccounts fx = g.getForeignAccounts();
        if (!(fx.getMoneyUsd(ForeignAccounts.GOLD) > 0)) throw new IllegalStateException("gold has no price: run with -Dcbs.world=<world-1.bin>");
        fx.buyReserves(coverShare * g.getCentralBank().m0(), "XAU");
        fx.forgetDealing();
        CurrencyRegimeCheck.quietly(() -> g.setCurrencyRegime(GOLD));
        for (int m = 0; m < months && fx.getRegime() == GOLD; m++) CurrencyRegimeCheck.play(g, 1);
        System.out.printf("%s: month %d, regime %s, mint D$%.4f, rate %.4f, centre %.4f, gold %.1f oz(k), cover %.3f, free US$%,.0fk, breaks %d%n",
                label, g.getMonth(), fx.getRegime(), fx.getMintPrice(), fx.getRate(), fx.goldCentre(), fx.getHolding(ForeignAccounts.GOLD),
                g.getGoldCover(), g.getFreeGoldUsd(), fx.getBreaks());
        return g;
    }
}
