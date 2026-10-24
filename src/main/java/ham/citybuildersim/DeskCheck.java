package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Set;

/**
 * The debt desk's figures (CD6, spec-city-debt.md sections 7 and 8): the figures the
 * debt screens used to work out for themselves, held to the model that now gives
 * them, and what the desk, a line's page and Your rate read beside them.
 *
 * WHY THIS EXISTS. Before the new screens, the old ones stopped working out model
 * figures (CLAUDE.md: every model figure through a public getter, never recomputed
 * in the screen), and three of those figures were not what their labels said. A
 * linked bond's last month counted its inflation uplift as "coupon a month"; the
 * Government tab's grid summed each live piece's last expense where the budget's
 * line holds the interest of a piece that fell due, and a dollar piece's booked at
 * the month's rate; the Close it now page worked out its own premium, honest line
 * and call gain. Each is a read, or a sum struck beside one already struck, and the
 * model's month does not move.
 *
 * What this has to prove:
 *
 * 1. A linked bond's last month: its coupon is the coupon proper (the real coupon on
 *    the indexed principal) and the uplift is its own getter, the face times the
 *    ratio less one; the old measure, getMonthlyInterestExpense(), is the two; a
 *    month earlier the uplift was nothing; the manager's sums are the pieces'; and
 *    the press pays the uplift read before it, with the coupon.
 * 2. The interest by kind is the budget's line: the five kinds' interest booked add
 *    up to the line, the month a bill matured (the bill is gone from the list and
 *    its interest is not, so the old sum of the live pieces falls short of the line
 *    by exactly the bill's) and a month the D$ moved after the strike (a dollar
 *    piece's expense read at the new rate is not the one the line holds); a saved
 *    and loaded city has each kind's figure back.
 * 3. A buyback with a gain, and the call: the journal's premium foots to the gain on
 *    carrying value; the honest line's four figures are what the buyback then does
 *    to the piece's coupon and what the price costs again at today's rate; the
 *    call's gain, read before the call, is what the books then take.
 * 4 onwards: the desk, a line's page and Your rate, slices B and C.
 *
 * Each fixture causes its condition rather than finding a city in it, and checks it
 * was caused. The cities are FloatLinkCheck's (it is borrowed whole: its played city,
 * its bank-held fixtures and its index), so this harness hands that class the two
 * streams it prints through.
 *
 * @author Jerus
 */
public class DeskCheck {

    static int fails = 0;
    static int asserts = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        asserts++;
        if (!ok) fails++;
        out.printf("%-104s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void near(String label, double actual, double expected, double tol) {
        asserts++;
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-104s FAIL  %,.9f != %,.9f (allowed %.1e)%n", label, actual, expected, tol);
        } else {
            out.printf("%-104s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    /** A month, pressed with its printing kept out of the log. */
    static void press(Game g) {
        quietly(g::toggleNextMonth);
    }

    static boolean wanted(String[] args, int section) {
        if (args.length == 0) return true;
        for (String a : args) if (a.equals(String.valueOf(section))) return true;
        return false;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        // FloatLinkCheck's fixture builders print through its own streams, which only its main() sets.
        FloatLinkCheck.out = out;
        FloatLinkCheck.quiet = quiet;
        long t0 = System.currentTimeMillis();

        if (wanted(args, 1)) lastMonthCoupon();
        if (wanted(args, 2)) interestByKind();
        if (wanted(args, 3)) buybackAndCall();

        out.printf("%n(%d assertions, %d s)%n", asserts, (System.currentTimeMillis() - t0 + 500) / 1000);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /* =====================================================================
       1. A LINKED BOND'S LAST MONTH
       ===================================================================== */

    static void lastMonthCoupon() {
        out.println("\n--- a linked bond's last month: the coupon is the coupon, the uplift its own ---");

        Game g = FloatLinkCheck.readyCity("desk-uplift", true, 0);
        DebtManager dm = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double l0 = dm.priceIndexLevel();
        double[] rising = new double[14];
        Arrays.fill(rising, 1.01);
        FloatLinkCheck.Index idx = new FloatLinkCheck.Index(l0, rising);
        dm.setPriceIndexSource(idx);
        // A year to live, a second linker with two to live, a floater: the manager's sums have pieces to add up.
        InflationLinkedBond linker = FloatLinkCheck.fixtureLinker(g, 20_000, 12, .02, l0);
        InflationLinkedBond longer = FloatLinkCheck.fixtureLinker(g, 10_000, 24, .015, l0);
        FloatingRateNote floater = FloatLinkCheck.fixtureFloater(g, 10_000, 24, .005);

        for (int k = 1; k <= 10; k++) {
            idx.k = k;
            press(g);
        }
        check("fixture: ten months on, the linker has two months left and the index has lifted its ratio over 1 ("
                + String.format("%.4f", linker.getRatio()) + ")", linker.getRemainingMonths() == 2 && linker.getRatio() > 1);
        near("a month before its last, the linker's uplift due is nothing", linker.getUpliftDue(), 0, 0);
        near("...and what it owes in coupon is its whole monthly interest expense", linker.getMonthlyCoupon(), linker.getMonthlyInterestExpense(), 1e-9);

        idx.k = 11;
        press(g);
        check("fixture: one more month, and the linker is in its last month, the ratio still over 1",
                linker.getRemainingMonths() == 1 && linker.getRatio() > 1 && dm.getDebt().contains(linker));

        // Read before the last press: the index is held flat for it, so the uplift read now is the uplift the press pays.
        double uplift = linker.getUpliftDue();
        double coupon = linker.getMonthlyCoupon();
        near("in its last month the uplift due is the face times the ratio less one, from the bond's own getters",
                uplift, linker.getFaceValue() * (linker.getRatio() - 1), 1e-9);
        check("...and it is something (" + String.format("%,.2f", uplift) + ")", uplift > 0);
        near("the coupon is the real coupon on the indexed principal, a twelfth: the uplift is not in it",
                coupon, linker.getIndexedPrincipal() * linker.getRealCoupon() / 12, 1e-9);
        near("the old measure, the monthly interest expense, is the coupon and the uplift together",
                linker.getMonthlyInterestExpense(), coupon + uplift, 1e-9);
        near("a linker with months to go, beside it, owes no uplift", longer.getUpliftDue(), 0, 0);
        near("...and a floater none", floater.getUpliftDue(), 0, 0);
        near("...and the floater's coupon is its whole monthly interest expense", floater.getMonthlyCoupon(), floater.getMonthlyInterestExpense(), 1e-9);

        double sumCoupon = 0, sumUplift = 0, sumOld = 0;
        for (Debt d : dm.getDebt()) {
            sumCoupon += d.getMonthlyCoupon();
            sumUplift += d.getUpliftDue();
            sumOld += d.getMonthlyInterestExpense();
        }
        near("the manager's coupon in cash next month is the pieces' coupons, summed", dm.getMonthlyCoupon(), sumCoupon, 1e-9);
        near("...and the uplift due next month is the pieces' uplifts, summed", dm.getUpliftDueNextMonth(), sumUplift, 1e-9);
        check("...the linker's among them", dm.getUpliftDueNextMonth() >= uplift - 1e-9);
        near("...so the two together are the old measure, the pieces' monthly interest expense summed",
                dm.getMonthlyCoupon() + dm.getUpliftDueNextMonth(), sumOld, 1e-6);

        press(g);
        check("the last press repays the linker: it is off the books", !dm.getDebt().contains(linker));
        near("the uplift it paid is the uplift read before the press", linker.getLastUpliftPaid(), uplift, 1e-9);
        near("...and the cash coupon it paid is the coupon and that uplift", linker.getLastCoupon(), coupon + uplift, 1e-9);
        near("...the manager's uplift paid that month is it", dm.getUpliftPaid(), uplift, 1e-9);
        near("...and the budget's uplift paid in cash is it, on its own line", na.getUpliftPaidInCash(), uplift, 1e-6);
        near("the longer linker, still on the books, owes no uplift yet", longer.getUpliftDue(), 0, 0);
    }

    /* =====================================================================
       2. THE INTEREST BY KIND IS THE BUDGET'S LINE
       ===================================================================== */

    static int onBooks(DebtManager dm, String type) {
        int n = 0;
        for (Debt d : dm.getDebt()) if (d.getType().equals(type)) n++;
        return n;
    }

    /** What the five kinds' interest booked add up to. */
    static double kindsSum(DebtManager dm) {
        double sum = 0;
        for (String kind : DebtManager.BOOK_KINDS) sum += dm.getInterestBookedOf(kind);
        return sum;
    }

    /** The old sum: every piece still on the list, its last interest expense read now. */
    static double liveSum(DebtManager dm) {
        double sum = 0;
        for (Debt d : dm.getDebt()) sum += d.getLastInterestExpense();
        return sum;
    }

    /** Sells one piece through its handler and says whether it went on the books as a new piece. */
    static Debt sold(Game g, String what, Runnable sale) {
        DebtManager dm = g.getDebtManager();
        Set<Debt> before = FloatLinkCheck.piecesOf(dm);
        quietly(sale);
        Debt piece = FloatLinkCheck.newest(dm, before);
        check("fixture: " + what + " was sold and is on the books", piece != null);
        return piece;
    }

    static void interestByKind() throws Exception {
        out.println("\n--- the interest by kind is the budget's line ---");

        double r0 = 1.25, r1 = r0 * 1.5;
        Game g = FloatLinkCheck.readyCity("desk-kinds", true, 0);
        DebtManager dm = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        g.getForeignAccounts().pinRate(r0);
        press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double ask = FloatLinkCheck.askOf(g, .1);

        Debt bill = sold(g, "a three-month bill", () -> g.handleTBillLogic(ask, 3, 100));
        sold(g, "a five-year amortizing bond", () -> g.handleMediumBondLogic(ask, 5, 100));
        sold(g, "a ten-year line", () -> g.handleLongBondLogic(ask, 10, 100));
        sold(g, "a two-year floater", () -> g.handleFloatingNoteLogic(ask, 2, 100));
        sold(g, "a ten-year linker", () -> g.handleLinkedBondLogic(ask, 10, 100));
        Debt usd = sold(g, "a five-year dollar bond", () -> g.handleForeignLogic("Term", 5_000, 5, 100, false));
        check("fixture: ...a bill, a serial bond, a term bond, a floater, a linker and a dollar piece are on the books",
                onBooks(dm, "NOTE") >= 1 && onBooks(dm, "SERIAL") >= 1 && onBooks(dm, "TERM") >= 1 && onBooks(dm, "FLOAT") >= 1
                        && onBooks(dm, "LINKED") >= 1 && usd != null && usd.isForeign());
        check("fixture: ...and the D$ is held at " + r0 + " for the dollar paper", g.getForeignAccounts().getRate() == r0 && usd != null && usd.getExchangeRate() == r0);

        // The months to the bill's maturity: each one's five kinds add up to the line, and the old sum matches it while no piece has fallen due.
        int guard = 0;
        double worst = 0;
        int months = 0;
        boolean oldMatched = true;
        while (onBooks(dm, "NOTE") > 0 && guard++ < 6) {
            press(g);
            if (onBooks(dm, "NOTE") == 0) break;
            months++;
            worst = Math.max(worst, Math.abs(kindsSum(dm) - na.getInterestExpense()));
            oldMatched &= Math.abs(liveSum(dm) - na.getInterestExpense()) <= 1e-6;
        }
        check("fixture: the bill fell due (" + bill.getDuration() + " months) and is gone from the list", bill.isMatured() && onBooks(dm, "NOTE") == 0);
        near("(a) in each of the " + months + " months before it, the five kinds add up to the budget's interest line", worst, 0, 1e-6);
        check("...and while every piece was still on the list, so did the old sum of their last expenses", oldMatched);

        // (a) The month the bill matured.
        double line = na.getInterestExpense();
        double bills = dm.getInterestBookedOf("NOTE");
        check("(a) the month the bill matured, the bills' interest is something with no bill on the books (" + String.format("%,.4f", bills) + ")", bills > 0);
        near("...the five kinds add up to the budget's interest line", kindsSum(dm), line, 1e-6);
        near("...the manager's total booked is that line too", dm.getInterestBooked(), line, 1e-6);
        check("...and the old sum, every live piece's last interest expense, falls short of the line", liveSum(dm) < line - 1e-6);
        near("...by exactly the bill's interest, which left the list with the bill", line - liveSum(dm), bills, 1e-6);

        // (b) The D$ moves after the strike, as the month's reprice moves it, and the debt is told at once.
        press(g);
        double usdAtStrike = usd.getLastInterestExpense();
        check("fixture: the dollar piece is on the books and its interest, in D$, is something (" + String.format("%,.4f", usdAtStrike) + ")",
                dm.getDebt().contains(usd) && usdAtStrike > 0);
        g.getForeignAccounts().pinRate(r1);
        dm.setExchangeRate(r1);
        g.getForeignAccounts().takeForeignDebt(dm.getForeignPrincipalUsd(), r1);
        check("fixture: the D$ moved by half and the paper was told", usd.getExchangeRate() == r1);
        double line2 = na.getInterestExpense();
        near("(b) a month the D$ moved, the five kinds still add up to the budget's interest line", kindsSum(dm), line2, 1e-6);
        double gap = liveSum(dm) - line2;
        double want = usd.getLastInterestExpense() * (1 - r0 / r1);
        check("...and the live pieces' sum at the new rate does not: the dollar piece's expense, read again, is over what was booked ("
                + String.format("%,.4f", gap) + ")", gap > 1e-6);
        near("...by the dollar piece's expense times the D$'s move, and nothing else", gap, want, 1e-6);

        // (c) Saved and loaded.
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = g.saveGame(10, "desk").ok);
        check("fixture: the city saved to slot 10 of its scratch root", ok[0]);
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        DebtManager loaded = back.getDebtManager();
        NationalAccounts naBack = back.getEconomyManager().getNationalAccounts();
        for (String kind : DebtManager.BOOK_KINDS) {
            near("(c) loaded, the " + kind + " kind's interest booked is the original's", loaded.getInterestBookedOf(kind), dm.getInterestBookedOf(kind), 0);
        }
        check("...and the kinds' figures were something that could be lost: the saved city had a kind that is not nothing",
                Arrays.stream(DebtManager.BOOK_KINDS).anyMatch(k -> dm.getInterestBookedOf(k) > 0));
        near("...they add up to the loaded city's own interest line", kindsSum(loaded), naBack.getInterestExpense(), 1e-6);
        near("...which is the original's", naBack.getInterestExpense(), na.getInterestExpense(), 1e-6);
        check("a save from before the field reads zeros: null and a wrong length restore nothing",
                restoresZeros(loaded));
    }

    /** DebtManager.restoreInterestByKind() with nothing and with the wrong length: zeros, not an error. */
    static boolean restoresZeros(DebtManager dm) {
        double[] keep = dm.interestByKindToSave();
        dm.restoreInterestByKind(null);
        boolean nullOk = kindsSum(dm) == 0;
        dm.restoreInterestByKind(keep);
        dm.restoreInterestByKind(new double[] {1, 2});
        boolean wrongOk = kindsSum(dm) == 0;
        dm.restoreInterestByKind(keep);
        return nullOk && wrongOk && Arrays.equals(dm.interestByKindToSave(), keep);
    }

    /* =====================================================================
       3. A BUYBACK WITH A GAIN: ITS JOURNAL, ITS HONEST LINE, AND THE CALL
       ===================================================================== */

    static void buybackAndCall() {
        out.println("\n--- a buyback with a gain: its journal, its honest line, and the call ---");

        Game g = FloatLinkCheck.readyCity("desk-buyback", true, 0);
        DebtManager dm = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        FloatLinkCheck.worldWantsPaper(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double ask = Math.max(100, g.getHouseholdBalance().spareForPaper() * 3);
        Debt line = sold(g, "a ten-year line", () -> g.handleLongBondLogic(ask, 10, 100));
        g.setCashForTest(Founding.WEALTHY_CASH);
        Debt callable = sold(g, "a callable ten-year line", () -> g.handleLongBondLogic(ask, 10, 100, Auction.Cap.DEFAULT, true));
        if (line == null || !(callable instanceof LongTermBond call)) return;
        press(g);

        // The rates go up: the line trades under what it is carried at, and the city names the most it will pay, what it is carried at.
        dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .05));
        g.strikeTheCurve();
        press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double carriedPer100 = line.getCarryingValue() / line.getOustandingPrincipal() * 100;
        Buyback.Result q = g.quoteBuyback(line, 1e12, carriedPer100);
        check("fixture: the line is worth less than the books carry it at (" + String.format("%.3f against %.3f per 100", q.fairPer100(), carriedPer100) + ")",
                q.fairPer100() < carriedPer100);
        check("fixture: ...and the city buys back at a gain on the carrying value", q.takenFace() > 0 && q.gainOnCarrying() > 0);

        // The journal.
        double principal = line.getOustandingPrincipal();
        double carried = line.getCarryingValue();
        near("the premium (below nothing, the discount) still on the books for the face taken is the line's, in the share taken",
                q.unamortisedTaken(), (carried - principal) * q.takenFace() / principal, 1e-6);
        near("...and is the carrying value of the face taken less that face", q.unamortisedTaken(), q.carriedTaken() - q.takenFace(), 1e-9);
        near("the journal foots: the face taken, that premium, less the cash paid, is the gain on carrying value",
                q.takenFace() + q.unamortisedTaken() - q.paid(), q.gainOnCarrying(), 1e-9);

        // The honest line.
        Buyback.HonestLine hl = g.honestLine(line, q);
        double coupon0 = line.getMonthlyCoupon();
        check("the honest line's months are what the piece has left (" + hl.months() + ")", hl.months() == line.getRemainingMonths() && hl.months() >= 1);
        near("...its rate is the curve's rate for those months", hl.rate(), dm.curveRate(hl.months()), 0);
        near("...the cost of borrowing the price again is the price at that rate for a month", hl.costAgain(), q.paid() * hl.rate() / 12, 1e-9);
        check("...the coupon it stops is something (" + String.format("%,.4f", hl.couponStopped()) + ")", hl.couponStopped() > 0);
        near("...and its change is what borrowing again costs less the coupon stopped", hl.change(), hl.costAgain() - hl.couponStopped(), 1e-12);
        Buyback.Result deal = g.buyBack(line, 1e12, carriedPer100);
        check("fixture: it was bought, in part, and the line is still on the books", deal.takenFace() > 0 && dm.getDebt().contains(line));
        near("the buyback took what the quote said", deal.takenFace(), q.takenFace(), 1e-6);
        near("then the piece's coupon fell by the coupon the honest line said it stops", coupon0 - line.getMonthlyCoupon(), hl.couponStopped(), 1e-6);
        press(g);
        near("the gain the books took for the buyback is the quote's gain on carrying value", na.getRetiredGain(), deal.gainOnCarrying(), 1e-6);

        // The call.
        g.setCashForTest(Founding.WEALTHY_CASH);
        call.moveFirstCallForTest(g.getMonth());
        check("fixture: the callable line settled and can be called now", g.canCall(call));
        double carriedCall = call.getCarryingValue();
        double gain = g.callGain(call);
        check("the call's gain is something to read: the line is carried away from its principal (" + String.format("%,.4f", gain) + ")", Math.abs(gain) > 1e-6);
        double paid = g.callLine(call);
        check("fixture: the call was made and the line is off the books", paid > 0 && !dm.getDebt().contains(call));
        near("the gain read before the call is the carrying value taken off the books less the cash the call paid", gain, carriedCall - paid, 1e-9);
        press(g);
        near("...and it is the gain the books took for the call", na.getRetiredGain(), gain, 1e-6);
        near("no gain for no line", g.callGain(null), 0, 0);
    }
}
