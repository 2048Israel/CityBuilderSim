package ham.citybuildersim;

import ham.citybuildersim.ui.RenderCD6Page;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;

/**
 * Scratch, not in the build (like RenderXR2): CD6's book and line pages as PNGs. Plays a scratch city (GameFiles.scratch(),
 * nothing of Jerus's is read or written) on DeskCheck's fixture - FloatLinkCheck's played city with paper of every kind: a bill,
 * an amortizing bond, a ten-year line that is sold and then reopened, a callable ten-year line, a thirty-year line, a floater, a
 * linker and a dollar bond - the policy rate raised after the sales and the months played on, so the price paths have a shape.
 * Writes the book (1,200 wide and 900 wide) and the page of a reopened line, a linker and a callable line; with a second
 * argument (a folder) it writes the other kinds' pages there too, for the eye. Then it plays the city on, the dial raised again, until
 * two years of curve are on the book, and writes Your rate (your-rate.png and your-rate-narrow.png), and a young city with a few
 * months on the book (your-rate-young.png: "not yet recorded"); with the folder, also a city with the central bank holding paper, one
 * with a year but not two, and the young city narrow.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/CD6/RenderCD6Page.java docs/queue/CD6/RenderCD6.java
 *   java -cp "<scratch>;target/classes;<classpath.txt>" ham.citybuildersim.RenderCD6 docs/queue/CD6 [<extras folder>]
 */
public class RenderCD6 {

    public static void main(String[] args) throws Exception {
        File dir = new File(args.length > 0 ? args[0] : "docs/queue/CD6");
        File extras = args.length > 1 ? new File(args[1]) : null;
        PrintStream real = System.out;
        PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
        DeskCheck.out = real;
        DeskCheck.quiet = quiet;
        FloatLinkCheck.out = real;
        FloatLinkCheck.quiet = quiet;
        javafx.application.Platform.startup(() -> { });

        Game g = FloatLinkCheck.readyCity("cd6-render", true, 0);
        DebtManager dm = g.getDebtManager();
        g.getForeignAccounts().pinRate(1.25);
        DeskCheck.press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double ask = FloatLinkCheck.askOf(g, .1);

        Debt term = sell(g, "ten-year line", () -> g.handleLongBondLogic(ask, 10, 100));
        Debt bill = sell(g, "three-month bill", () -> g.handleTBillLogic(ask, 3, 100));
        Debt serial = sell(g, "five-year amortizing bond", () -> g.handleMediumBondLogic(ask, 5, 100));
        Debt call = sell(g, "callable ten-year line", () -> g.handleLongBondLogic(ask, 10, 100, Auction.Cap.DEFAULT, true));
        Debt thirty = sell(g, "thirty-year line", () -> g.handleLongBondLogic(ask, 30, 100));
        Debt floater = sell(g, "two-year floater", () -> g.handleFloatingNoteLogic(ask, 2, 100));
        Debt linker = sell(g, "ten-year linker", () -> g.handleLinkedBondLogic(ask, 10, 100));
        Debt usd = sell(g, "five-year dollar bond", () -> g.handleForeignLogic("Term", 5_000, 5, 100, false));

        stage(g, "after the sales");
        // Some months on, then the line reopened: one line, two sales.
        for (int i = 0; i < 5; i++) DeskCheck.press(g);
        FloatLinkCheck.worldWantsPaper(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        int pieces = dm.getDebt().size();
        DeskCheck.quietly(() -> g.handleLongBondLogic(ask, 10, 100));
        System.out.println("reopened the ten-year line: " + (dm.getDebt().size() == pieces && term != null && term.getSales().size() == 2
                ? "yes, two sales" : "NO - " + (term == null ? "no line" : term.getSales().size() + " sales")));

        stage(g, "after the reopening");
        // The rates up after the sales, the months played on: the paper sold near par trades under what it is carried at.
        DeskCheck.quietly(() -> dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .01)));
        g.strikeTheCurve();
        stage(g, "the dial raised");
        for (int i = 0; i < 9; i++) DeskCheck.press(g);
        stage(g, "nine months on");
        DeskCheck.quietly(() -> dm.setPolicyRate(Math.max(.005, dm.getPolicyRate() - .005)));
        g.strikeTheCurve();
        for (int i = 0; i < 4; i++) DeskCheck.press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);

        System.out.printf("the dial %.4f, the city's rate %.4f%n", dm.getPolicyRate(), g.getInterestRate());
        System.out.printf("the book: %d lines, month %d, the ten-year line's path %d months%n", dm.getDebt().size(), g.getMonth(),
                term == null ? -1 : term.getPricePath().length);
        RenderCD6Page.snapshot(g, null, 1200, new File(dir, "book-desk.png"));
        RenderCD6Page.snapshot(g, null, 900, new File(dir, "book-desk-narrow.png"));
        if (term != null) RenderCD6Page.snapshot(g, term, 1200, new File(dir, "line-bond.png"));
        if (linker != null) RenderCD6Page.snapshot(g, linker, 1200, new File(dir, "line-linked.png"));
        if (call != null) RenderCD6Page.snapshot(g, call, 1200, new File(dir, "line-callable.png"));
        if (extras != null) {
            extras.mkdirs();
            RenderCD6Page.snapshot(g, term, 900, new File(extras, "line-bond-narrow.png"));
            if (bill != null) RenderCD6Page.snapshot(g, bill, 1200, new File(extras, "line-bill.png"));
            if (serial != null) RenderCD6Page.snapshot(g, serial, 1200, new File(extras, "line-serial.png"));
            if (thirty != null) RenderCD6Page.snapshot(g, thirty, 1200, new File(extras, "line-thirty.png"));
            if (floater != null) RenderCD6Page.snapshot(g, floater, 1200, new File(extras, "line-floater.png"));
            if (usd != null) RenderCD6Page.snapshot(g, usd, 1200, new File(extras, "line-dollar.png"));
            if (linker != null) RenderCD6Page.snapshot(g, linker, 900, new File(extras, "line-linked-narrow.png"));
        }
        // Your rate (board 5): the dial moved on again and the months played, until two years of curve are on the book - two years
        // ago the dial was low, a year ago it was higher, today higher still, so the three curves differ.
        DeskCheck.quietly(() -> dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .015)));
        g.strikeTheCurve();
        stage(g, "the dial raised again");
        for (int i = 0; i < 10; i++) DeskCheck.press(g);
        stage(g, "ten months on");
        System.out.printf("months played after the sales: the city is in month %d%n", g.getMonth());
        report(g);
        RenderCD6Page.snapshotYourRate(g, 1200, new File(dir, "your-rate.png"));
        RenderCD6Page.snapshotYourRate(g, 900, new File(dir, "your-rate-narrow.png"));
        if (extras != null) {
            // The central bank holding term paper, for the eye: the bar's outlined stretch is what it takes off the premium.
            DeskCheck.quietly(() -> g.getCentralBank().setTargetShare(.3));
            for (int i = 0; i < 6; i++) DeskCheck.press(g);
            report(g);
            RenderCD6Page.snapshotYourRate(g, 1200, new File(extras, "your-rate-qe.png"));
        }

        // A young city: a few months on the book, no year to look back on.
        GameFiles files = GameFiles.scratch("cd6-render-young");
        Game young = new Game(files);
        DeskCheck.quietly(() -> {
            young.run();
            young.simulateMonths(6);
        });
        System.out.println("the young city: month " + young.getMonth());
        report(young);
        RenderCD6Page.snapshotYourRate(young, 1200, new File(dir, "your-rate-young.png"));
        if (extras != null) {
            // A city with a year but not two, for the eye.
            Game year = new Game(GameFiles.scratch("cd6-render-year"));
            DeskCheck.quietly(() -> {
                year.run();
                year.simulateMonths(15);
            });
            report(year);
            RenderCD6Page.snapshotYourRate(year, 1200, new File(extras, "your-rate-year.png"));
            RenderCD6Page.snapshotYourRate(young, 900, new File(extras, "your-rate-young-narrow.png"));
        }
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /** What the page draws from, in one line each: the dial and the rule, the curve's past, the parts of the ten-year rate and the real rate. */
    static void report(Game g) {
        DebtManager dm = g.getDebtManager();
        System.out.printf("  dial %.4f rule %.4f year %.4f five %.4f settles %.4f (path end %.4f) | struck inflation %.4f expected %.4f target %.4f credibility %.3f%n",
                dm.getPolicyRate(), dm.ruleNow(), dm.expectedDialIn(12), dm.expectedDialIn(60), dm.settledDial(),
                dm.expectedDialIn(YieldCurve.HORIZON), dm.getStruckInflation(), dm.getStruckExpected(), dm.getInflationTarget(), g.getCredibility());
        for (int ago : new int[] {0, 12, 24}) {
            double[] row = dm.curveMonthsAgo(ago);
            System.out.println("  curve " + ago + " months ago: " + (row == null ? "not recorded" : String.format("dial %.4f, 3m %.4f 1y %.4f 5y %.4f 10y %.4f 30y %.4f",
                    dm.policyRateMonthsAgo(ago), row[0], row[2], row[4], row[5], row[7])));
        }
        double parts = dm.expectedDialOver(120) + dm.creditSpread() + DebtManager.termPremium(120) - dm.compression(120);
        System.out.printf("  ten-year %.6f = parts %.6f (compression %.4f); real %.4f + priced %.4f = par %.4f%n", dm.curveRate(120), parts, dm.compression(120),
                dm.fairRealCoupon(120, 0), dm.breakevenInflation(120, 0), dm.parYield(120, 0));
    }

    /** Says where the dial and the city's curve stand. */
    static void stage(Game g, String when) {
        DebtManager dm = g.getDebtManager();
        YieldCurve curve = dm.curve();
        System.out.printf("%-22s month %d, dial %.4f (rule %.4f, inflation %.4f), rate %.4f, curve 3m %.4f 1y %.4f 5y %.4f 10y %.4f 30y %.4f%n", when,
                g.getMonth(), dm.getPolicyRate(), dm.ruleNow(), dm.getStruckInflation(), g.getInterestRate(), curve.zero(3), curve.zero(12),
                curve.zero(60), curve.zero(120), curve.zero(360));
    }

    /** Runs a sale and returns the piece it put on the books (null, and says so, when it put none). */
    static Debt sell(Game g, String what, Runnable sale) {
        DebtManager dm = g.getDebtManager();
        Set<Debt> before = FloatLinkCheck.piecesOf(dm);
        DeskCheck.quietly(sale);
        Debt piece = FloatLinkCheck.newest(dm, before);
        System.out.println(what + ": " + (piece == null ? "NOT SOLD" : "sold, " + piece.getType() + ", " + piece.getRemainingMonths() + " months"));
        return piece;
    }
}
