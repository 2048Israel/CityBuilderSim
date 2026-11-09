package ham.citybuildersim;

import ham.citybuildersim.ui.RenderBB1Page;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;

/**
 * Scratch, not in the build (like RenderCD6): BB1's Close it now page as PNGs. Plays a scratch city (GameFiles.scratch(), nothing
 * of Jerus's is read or written) on the one RenderCD6 plays - DeskCheck's fixture, FloatLinkCheck's played city with paper of every
 * kind sold, a ten-year line reopened, the policy rate raised and the months played on - and writes the page of the household-held
 * ten-year line all on offer (close-all.png), at a half (close-half.png), 900 wide (close-narrow.png), 520 wide at a half (close-narrowest.png, the chips wrapping under the box) and of the callable ten-year
 * line after its first call month is moved to now and a month played (close-callable.png). Says, for each, what the desk shows at
 * fair and what the auction would book.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/BB1/RenderBB1Page.java docs/queue/BB1/RenderBB1.java
 *   java -cp "<scratch>;target/classes;<classpath.txt>" ham.citybuildersim.RenderBB1 docs/queue/BB1
 */
public class RenderBB1 {

    public static void main(String[] args) throws Exception {
        File dir = new File(args.length > 0 ? args[0] : "docs/queue/BB1");
        PrintStream real = System.out;
        PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
        DeskCheck.out = real;
        DeskCheck.quiet = quiet;
        FloatLinkCheck.out = real;
        FloatLinkCheck.quiet = quiet;
        javafx.application.Platform.startup(() -> { });

        Game g = FloatLinkCheck.readyCity("bb1-render", true, 0);
        DebtManager dm = g.getDebtManager();
        g.getForeignAccounts().pinRate(1.25);
        DeskCheck.press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double ask = FloatLinkCheck.askOf(g, .1);

        Debt term = sell(g, "ten-year line", () -> g.handleLongBondLogic(ask, 10, 100));
        sell(g, "three-month bill", () -> g.handleTBillLogic(ask, 3, 100));
        sell(g, "five-year amortizing bond", () -> g.handleMediumBondLogic(ask, 5, 100));
        Debt call = sell(g, "callable ten-year line", () -> g.handleLongBondLogic(ask, 10, 100, Auction.Cap.DEFAULT, true));
        sell(g, "thirty-year line", () -> g.handleLongBondLogic(ask, 30, 100));
        sell(g, "two-year floater", () -> g.handleFloatingNoteLogic(ask, 2, 100));
        sell(g, "ten-year linker", () -> g.handleLinkedBondLogic(ask, 10, 100));
        sell(g, "five-year dollar bond", () -> g.handleForeignLogic("Term", 5_000, 5, 100, false));

        // Some months on, then the line reopened: one line, two sales.
        for (int i = 0; i < 5; i++) DeskCheck.press(g);
        FloatLinkCheck.worldWantsPaper(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        DeskCheck.quietly(() -> g.handleLongBondLogic(ask, 10, 100));
        // The rates up after the sales, the months played on: the paper sold near par trades under what it is carried at.
        DeskCheck.quietly(() -> dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .01)));
        g.strikeTheCurve();
        for (int i = 0; i < 9; i++) DeskCheck.press(g);
        DeskCheck.quietly(() -> dm.setPolicyRate(Math.max(.005, dm.getPolicyRate() - .005)));
        g.strikeTheCurve();
        for (int i = 0; i < 4; i++) DeskCheck.press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        System.out.printf("month %d, the dial %.4f, the city's rate %.4f%n", g.getMonth(), dm.getPolicyRate(), g.getInterestRate());

        // CD6's render read green at fair and red at the auction (+D$5.1M and -D$2.1M, the households holding 86%): the dial raised, two months
        // played on each raise, until the ten-year line reads a gain at fair and a loss at the auction, or ten raises have been tried.
        for (int i = 0; term != null && i < 10; i++) {
            Buyback.Result r = g.quoteBuyback(term, g.buybackOfferable(term), Double.NaN);
            if (r.gainAtFair() > 0 && r.gainOnCarrying() < 0) break;
            DeskCheck.quietly(() -> dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .02)));
            g.strikeTheCurve();
            DeskCheck.press(g);
            DeskCheck.press(g);
            g.setCashForTest(Founding.WEALTHY_CASH);
            say(g, term, "raise " + (i + 1) + " (dial " + String.format("%.4f", dm.getPolicyRate()) + ", rate " + String.format("%.4f", g.getInterestRate()) + ")", 1);
        }
        if (term != null) {
            say(g, term, "the ten-year line, all on offer", 1);
            say(g, term, "the ten-year line, a half", .5);
            RenderBB1Page.snapshotClose(g, term, 1, 1200, new File(dir, "close-all.png"));
            RenderBB1Page.snapshotClose(g, term, .5, 1200, new File(dir, "close-half.png"));
            RenderBB1Page.snapshotClose(g, term, 1, 900, new File(dir, "close-narrow.png"));
            RenderBB1Page.snapshotClose(g, term, .5, 520, new File(dir, "close-narrowest.png"));
        }
        if (call != null) {
            say(g, call, "the callable line, before its call month", 1);
            ((LongTermBond) call).moveFirstCallForTest(g.getMonth());
            DeskCheck.press(g);
            g.setCashForTest(Founding.WEALTHY_CASH);
            System.out.println("the callable line can be called: " + g.canCall(call) + "; first call month " + ((LongTermBond) call).getFirstCallMonth()
                    + ", this month " + g.getMonth());
            say(g, call, "the callable line, callable now", 1);
            RenderBB1Page.snapshotClose(g, call, 1, 1200, new File(dir, "close-callable.png"));
        }
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /** What the desk shows at fair, and what the auction would book, for one ticket: the figures the page draws. */
    static void say(Game g, Debt d, String what, double share) {
        double face = g.buybackOfferable(d) * share;
        Buyback.Result q = g.quoteBuyback(d, face, Double.NaN);
        double held = d.getOustandingPrincipal() > 0 ? d.householdPrincipal() / d.getOustandingPrincipal() : 0;
        System.out.printf("%s: face %,.0f of %,.0f, the households hold %.0f%% of it; carried (face taken) %,.1f, at fair %+,.1f (%+.1f%%), at the asks %+,.1f (%+.1f%%), "
                + "over fair %,.1f (households %,.1f, businesses %,.1f, world %,.1f, bank %,.1f); stop %.3f, fair %.3f%n",
                what, q.takenFace(), d.getOustandingPrincipal(), held * 100, q.carriedTaken(), q.gainAtFair(), q.gainAtFairShare() * 100,
                q.gainOnCarrying(), q.gainOnCarryingShare() * 100, q.overFair(), q.overFair(Buyback.Holder.HOUSEHOLDS),
                q.overFair(Buyback.Holder.FIRMS), q.overFair(Buyback.Holder.WORLD), q.overFair(Buyback.Holder.BANK), q.stopPer100(), q.fairPer100());
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
