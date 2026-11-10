package ham.citybuildersim;

import ham.citybuildersim.ui.RenderGD2Page;
import java.io.File;

/**
 * Scratch, not in the build (like RenderXR2): GD2's screens as PNGs. Plays a scratch city (GameFiles.scratch(), nothing of Jerus's
 * is read or written) on WorldMoneysCheck's trading town with the world file (-Dcbs.earth, -Dcbs.world), 30 months under the
 * default mix, buys euros and yen and then gold by name, chooses the mix with gold's share (and the euro's), plays two months on, and
 * writes Trade &gt; The reserves' cards: the vault's table with gold's row (vault-gold.png, and at 520 wide vault-gold-narrow.png,
 * and with its fold open vault-gold-years.png), the exchange with Gold chosen in the picker and an amount staged (exchange-gold.png,
 * and selling gold, sell-gold.png) and the exchange with the mix chosen and nothing staged (mix-gold.png). The JavaFX half is
 * ui.RenderGD2Page, because the screens are package-private.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/GD2/RenderGD2Page.java docs/queue/GD2/RenderGD2.java
 *   java -Dcbs.earth=C:/.../data/earth-2.bin -Dcbs.world=C:/.../data/world-1.bin -cp "<scratch>;target/classes;<classpath.txt>" ham.citybuildersim.RenderGD2 docs/queue/GD2
 */
public class RenderGD2 {
    public static void main(String[] args) throws Exception {
        File dir = new File(args.length > 0 ? args[0] : "docs/queue/GD2");
        if (WorldData.locate() == null) throw new IllegalStateException("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>)");
        javafx.application.Platform.startup(() -> { });

        Game g = WorldMoneysCheck.town("gd2-render");
        for (int m = 0; m < 30; m++) WorldMoneysCheck.play(g);
        ForeignAccounts fx = g.getForeignAccounts();
        double spend = Math.min(60_000, g.getCash() / 10);
        WorldMoneysCheck.quietly(() -> {
            g.buyForeignCurrency(spend, "EUR");
            g.buyForeignCurrency(spend / 2, "JPY");
            g.buyForeignCurrency(spend * 2, "XAU");
            fx.setReserveMix(ForeignAccounts.Mix.CHOSEN);
            fx.shiftChosen(ForeignAccounts.GOLD, .15);
            fx.shiftChosen(1, .20);
        });
        WorldMoneysCheck.play(g);
        WorldMoneysCheck.play(g);
        System.out.printf("month %d: gold US$%.4f an ounce, held %.1f thousand oz, vault US$%,.0fk, mix %s, shares %s%n", g.getMonth(),
                fx.getMoneyUsd(ForeignAccounts.GOLD), fx.getHolding(ForeignAccounts.GOLD), fx.getReservesUsd(), fx.getReserveMix(),
                java.util.Arrays.toString(fx.getChosenShares()));

        RenderGD2Page.snapshot(g, "vault", null, true, 0, false, 900, new File(dir, "vault-gold.png"));
        RenderGD2Page.snapshot(g, "vault", null, true, 0, false, 520, new File(dir, "vault-gold-narrow.png"));
        RenderGD2Page.snapshot(g, "vault", null, true, 0, true, 900, new File(dir, "vault-gold-years.png"));
        double stage = g.getCash() / 6;
        RenderGD2Page.snapshot(g, "exchange", "XAU", true, stage, false, 900, new File(dir, "exchange-gold.png"));
        RenderGD2Page.snapshot(g, "exchange", "XAU", false, fx.sellableReserves("XAU") / 4, false, 900, new File(dir, "sell-gold.png"));
        RenderGD2Page.snapshot(g, "exchange", null, true, 0, false, 900, new File(dir, "mix-gold.png"));
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
