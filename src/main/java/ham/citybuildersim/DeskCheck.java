package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
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
 * 4. The desk is the model's figures (slice B): each line of BookDesk is the ledger's own
 *    (price, yield, worth, carried, the rate the books charge it at, a linker's real yield),
 *    the totals are the ledger's and the bars by kind add up to them; what a point takes off
 *    a line is what the curve built a point higher takes off its payments, and a floater's
 *    duration is its month to the next reset, a ten-year line's most of its life.
 * 5. A line's next six months on the books are the months it then books: Debt.booksAhead()
 *    read, then six presses, a line sold under par, an amortizing bond across an anniversary
 *    slice, a bill to maturity (closing to nothing), a line never sold, a floater and a linker
 *    in its last month (the uplift in the coupon).
 * 6. A line remembers its price and its sales: one price entry a month since the sale, the
 *    last just what the ledger reads; a reopening is a second sale on one line; a part bought
 *    back keeps both; a change of unit scales the sales' money and not the per-100 path; the
 *    central bank's sales and the rollover's read as such; saved and loaded, the same, and
 *    the next month adds one entry.
 * 7. The curve's past: 26 months with the dial moved partway, the curve a year and two years
 *    ago as the harness read it then, none beyond what is kept; saved and loaded, the same;
 *    the rule and the dial the market expects; Your rate's nominal par yield against the
 *    linked line's real coupon and breakeven, and the ten-year rate as its four parts; and
 *    the TRACE GUARD, twin cities one of which records and one of which does not, alike to
 *    the bit for the 26 months.
 * 8. What closing a line now would book (Jerus, 2026-10-23: the book is green or red, in D$
 *    and per cent): a line at a gain and a line at a loss, both caused (a line sold at par and
 *    the rates raised, one sold at the higher rates and the rates cut) and both held by the bank
 *    alone, which offers at fair; each line's ifClosedNow() and ifClosedNowShare() read from the
 *    desk, then the line bought back whole and the gain the buyback booked (the deal's, and the
 *    month's gain on debt retired) is the figure and its share; a dollar line's figure is in D$
 *    at the month's actual rate; and the book's share is its total gain over its total carrying
 *    value.
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
        if (wanted(args, 4)) theDesk();
        if (wanted(args, 5)) booksAhead();
        if (wanted(args, 6)) remembers();
        if (wanted(args, 7)) theCurvesPast();
        if (wanted(args, 8)) whatClosingBooks();

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

    /* =====================================================================
       A HELPER FOR THE CASES THAT HOLD AT EVERY PIECE: THE WORST GAP, REPORTED ONCE
       ===================================================================== */

    /** The worst gap over a set of cases, reported as one assertion with how many cases it covers. */
    static final class Worst {
        int n;
        double worst;
        String at = "";

        void add(double actual, double expected, String where) {
            n++;
            double e = Math.abs(actual - expected);
            if (Double.isNaN(e)) e = Double.POSITIVE_INFINITY;
            if (e > worst) {
                worst = e;
                at = where;
            }
        }

        void report(String label, double tol) {
            asserts++;
            boolean ok = n > 0 && worst <= tol;
            if (!ok) fails++;
            out.printf("%-104s %s%n", label + " (" + n + " cases)",
                    ok ? "OK" : n == 0 ? "FAIL  no case ran" : "FAIL  worst " + worst + " at " + at);
        }
    }

    /* =====================================================================
       4. THE DESK IS THE MODEL'S FIGURES
       ===================================================================== */

    /** The curve a point higher, built the way the market builds one: its forwards each up a point, the shape the same, no spread and no ceiling to bind. */
    static YieldCurve aPointHigher(YieldCurve base) {
        double[] path = new double[YieldCurve.HORIZON];
        for (int t = 0; t < path.length; t++) path[t] = base.forward(t) + DebtManager.A_POINT;
        return new YieldCurve(path, 0, 0, path[0] + 1e6, base::shape);
    }

    static BookDesk.Line lineOf(BookDesk desk, Debt d) {
        for (BookDesk.Line l : desk.lines()) if (l.debt() == d) return l;
        return null;
    }

    static void theDesk() throws Exception {
        out.println("\n--- the desk is the model's figures ---");

        Game g = FloatLinkCheck.readyCity("desk-desk", true, 0);
        DebtManager dm = g.getDebtManager();
        g.getForeignAccounts().pinRate(1.25);
        press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double ask = FloatLinkCheck.askOf(g, .1);
        Debt bill = sold(g, "a three-month bill", () -> g.handleTBillLogic(ask, 3, 100));
        Debt serial = sold(g, "a five-year amortizing bond", () -> g.handleMediumBondLogic(ask, 5, 100));
        Debt term = sold(g, "a ten-year line", () -> g.handleLongBondLogic(ask, 10, 100));
        Debt call = sold(g, "a callable ten-year line", () -> g.handleLongBondLogic(ask, 10, 100, Auction.Cap.DEFAULT, true));
        Debt floater = sold(g, "a two-year floater", () -> g.handleFloatingNoteLogic(ask, 2, 100));
        Debt linker = sold(g, "a ten-year linker", () -> g.handleLinkedBondLogic(ask, 10, 100));
        Debt usd = sold(g, "a five-year dollar bond", () -> g.handleForeignLogic("Term", 5_000, 5, 100, false));
        if (bill == null || serial == null || term == null || call == null || floater == null || linker == null || usd == null) return;
        check("fixture: the callable line is callable and the dollar piece is written in dollars",
                call instanceof LongTermBond c && c.isCallable() && usd.isForeign());

        // The rates go up after the sales: the paper sold near par now trades under what it is carried at (caused, and checked below).
        dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .05));
        g.strikeTheCurve();
        press(g);

        double rateBefore = dm.getRate();
        BookDesk desk = g.bookDesk();
        BookDesk again = BookDesk.of(dm);
        check("fixture: the desk has a line for every piece on the books (" + dm.getDebt().size() + ")",
                desk.lines().size() == dm.getDebt().size() && dm.getDebt().stream().allMatch(d -> lineOf(desk, d) != null));
        check("reading the desk again moves nothing and gives the same figures: it is a read",
                dm.getRate() == rateBefore && again.worth() == desk.worth() && again.valueOfAPoint() == desk.valueOfAPoint()
                        && again.lines().equals(desk.lines()));

        // Each line is the ledger's own figures.
        Worst face = new Worst(), coupon = new Worst(), price = new Worst(), yld = new Worst(), worth = new Worst(), carried = new Worst(),
                booked = new Worst(), close = new Worst(), vsFace = new Worst(), months = new Worst(), point = new Worst(),
                dur = new Worst(), pricePar = new Worst(), reprice = new Worst(), shares = new Worst(), alone = new Worst();
        Worst pointVsHigher = new Worst();
        YieldCurve home = dm.curve(), homeHigh = aPointHigher(home);
        YieldCurve world = dm.worldCurveAt(dm.foreignRate()), worldHigh = aPointHigher(world);
        int realYields = 0, noRealYield = 0;
        for (Debt d : dm.getDebt()) {
            BookDesk.Line l = lineOf(desk, d);
            String name = d.getType() + "/" + d.getRemainingMonths();
            face.add(l.face(), d.getOustandingPrincipal(), name);
            coupon.add(l.coupon(), d.getMonthlyCoupon(), name);
            price.add(l.pricePer100(), dm.priceOfPar(d), name);
            yld.add(l.yield(), dm.yieldOf(d), name);
            worth.add(l.worth(), dm.marketValue(d), name);
            carried.add(l.carried(), d.getCarryingValue(), name);
            booked.add(l.bookedRate(), d.getEffectiveRate(), name);
            close.add(l.ifClosedNow(), d.getCarryingValue() - dm.marketValue(d), name);
            vsFace.add(l.vsFace(), d.getOustandingPrincipal() - dm.marketValue(d), name);
            months.add(l.monthsLeft(), d.getRemainingMonths(), name);
            point.add(l.valueOfAPoint(), dm.valueOfAPoint(d), name);
            dur.add(l.duration(), dm.durationOf(d), name);
            // ...and the figures that are the model's own, worked out another way here.
            pricePar.add(l.pricePer100(), l.worth() / l.face() * 100, name);
            reprice.add(Debt.presentValue(dm.pricingFlows(d), l.yield()), l.worth(), name);
            shares.add(l.households() + l.bank() + l.centralBank() + l.firms() + l.world(), 1, name);
            if (d.isForeign()) alone.add(l.world(), 1, name);
            // What a point takes off, from the curve built a point higher and priced the way marketValue() prices (not a floater's, whose coupons follow the dial).
            if (!(d instanceof FloatingRateNote)) {
                YieldCurve lo = d.isForeign() ? world : home, hi = d.isForeign() ? worldHigh : homeHigh;
                double[] flows = dm.pricingFlows(d);
                double taken = lo.pv(flows) - hi.pv(flows);
                pointVsHigher.add(l.valueOfAPoint() / taken, 1, name);
            }
            if (d instanceof InflationLinkedBond lk) {
                realYields++;
                check("the linker's real yield is the ledger's, a yield with the inflation taken out (" + String.format("%.4f", l.realYield()) + " against its nominal " + String.format("%.4f", l.yield()) + ")",
                        l.realYield() == dm.realYieldOf(lk) && Double.isFinite(l.realYield()));
            } else if (Double.isNaN(l.realYield())) {
                noRealYield++;
            }
        }
        face.report("each line's face is what is still owed on the piece", 1e-9);
        coupon.report("...its coupon the piece's own coupon a month (a linker's last month without its uplift)", 1e-9);
        price.report("...its price per 100 the ledger's priceOfPar()", 1e-9);
        yld.report("...its yield the ledger's yieldOf()", 1e-12);
        worth.report("...its worth the ledger's marketValue()", 1e-9);
        carried.report("...what it is carried at the piece's carrying value", 1e-9);
        booked.report("...the issue yield the piece's effective rate: the rate the books charge it at", 0);
        months.report("...the months it has left the piece's", 0);
        pricePar.report("...the price per 100 is its worth over its face (worked out here)", 1e-9);
        reprice.report("...and its yield prices its payments, flat, back to its worth (worked out here)", 1e-4);
        close.report("if closed now is the carrying value less the worth", 1e-9);
        vsFace.report("...and vs face the face less the worth", 1e-9);
        shares.report("the holders are shares of the face and add up to it", 1e-9);
        alone.report("...a dollar piece is the world's alone", 0);
        check("the real yield is a linker's alone: " + realYields + " linker, and NaN on the other " + noRealYield, realYields == 1 && noRealYield == dm.getDebt().size() - 1);

        // The totals are the ledger's.
        near("the desk's face is the ledger's principal", desk.face(), dm.getAllPrincipal(), 0);
        near("...its carried the ledger's carrying value", desk.carried(), dm.getCarryingValue(), 0);
        near("...its worth the ledger's total market value", desk.worth(), dm.getTotalMarketValue(), 0);
        near("...its coupon the ledger's coupon a month", desk.coupon(), dm.getMonthlyCoupon(), 0);
        double sumFace = 0, sumCarried = 0, sumWorth = 0, sumPoint = 0, sumCoupon = 0;
        for (BookDesk.Line l : desk.lines()) {
            sumFace += l.face();
            sumCarried += l.carried();
            sumWorth += l.worth();
            sumPoint += l.valueOfAPoint();
            sumCoupon += l.coupon();
        }
        near("the lines add up to the desk's face", sumFace, desk.face(), 1e-6);
        near("...its carried", sumCarried, desk.carried(), 1e-6);
        near("...its worth", sumWorth, desk.worth(), 1e-6);
        near("...its coupon", sumCoupon, desk.coupon(), 1e-6);
        near("...and what a point takes off", sumPoint, desk.valueOfAPoint(), 1e-6);
        near("if closed now, for the book, is its carrying value less its worth", desk.ifClosedNow(), dm.getCarryingValue() - dm.getTotalMarketValue(), 1e-9);
        near("...and vs face its face less its worth", desk.vsFace(), dm.getAllPrincipal() - dm.getTotalMarketValue(), 1e-9);
        double faceBars = 0, carriedBars = 0, worthBars = 0;
        boolean everyKind = true;
        for (String kind : DebtManager.BOOK_KINDS) {
            faceBars += desk.faceOf(kind);
            carriedBars += desk.carriedOf(kind);
            worthBars += desk.worthOf(kind);
            everyKind &= desk.faceOf(kind) > 0 && desk.carriedOf(kind) > 0 && desk.worthOf(kind) > 0;
        }
        check("fixture: every kind of paper is on the book, so every bar is something", everyKind);
        near("the three bars by kind: the faces add up to the desk's face", faceBars, desk.face(), 1e-6);
        near("...the carried to its carried", carriedBars, desk.carried(), 1e-6);
        near("...the worth to its worth", worthBars, desk.worth(), 1e-6);
        double termFace = 0;
        for (BookDesk.Line l : desk.lines()) if (l.debt().getType().equals("TERM")) termFace += l.face();
        near("a kind's bar is its pieces' lines: the bonds' face is the ten-year line's, the callable line's and the dollar bond's, and no other",
                desk.faceOf("TERM"), termFace, 1e-6);
        check("...and those three are in it", termFace >= lineOf(desk, term).face() + lineOf(desk, call).face() + lineOf(desk, usd).face() - 1e-6);

        // By when it falls due.
        boolean sorted = true;
        for (int i = 1; i < desk.lines().size(); i++) sorted &= desk.lines().get(i - 1).monthsLeft() <= desk.lines().get(i).monthsLeft();
        check("the lines are by when they fall due: the bill first, the longest last", sorted && desk.lines().get(0).debt() == bill
                && desk.lines().get(desk.lines().size() - 1).monthsLeft() == desk.lines().stream().mapToInt(BookDesk.Line::monthsLeft).max().getAsInt());

        // The case was caused: the paper sold near par trades under what it is carried at.
        BookDesk.Line termLine = lineOf(desk, term), serialLine = lineOf(desk, serial), floatLine = lineOf(desk, floater), linkLine = lineOf(desk, linker);
        check("fixture: the rates rose after the sales, so the ten-year line trades under its carrying value (" + String.format("%.2f against %.2f", termLine.worth(), termLine.carried()) + ")",
                termLine.worth() < termLine.carried());
        check("if closed now, the ten-year line is a gain: carried over worth (" + String.format("%,.2f", termLine.ifClosedNow()) + ")", termLine.ifClosedNow() > 0);
        check("...as is the amortizing bond (" + String.format("%,.2f", serialLine.ifClosedNow()) + "), and the book as a whole (" + String.format("%,.2f", desk.ifClosedNow()) + ")",
                serialLine.ifClosedNow() > 0 && desk.ifClosedNow() > 0);
        check("vs face, the line is worth less than is owed on it (" + String.format("%,.2f", termLine.vsFace()) + ")", termLine.vsFace() > 0);

        // The coupon on the paper.
        check("the coupon rate is the rate printed: the line's, the amortizing bond's, the linker's real coupon, the floater's margin, and a bill's nothing",
                term instanceof LongTermBond tb && termLine.couponRate() == tb.couponRate()
                        && serial instanceof MediumTermBond sb && serialLine.couponRate() == sb.couponRate()
                        && linker instanceof InflationLinkedBond lb && linkLine.couponRate() == lb.getRealCoupon()
                        && floater instanceof FloatingRateNote fb && floatLine.couponRate() == fb.getMargin()
                        && lineOf(desk, bill).couponRate() == 0);
        check("a bill's coupon is nothing, and a bill's yield is its discount's: the books charge it more than nothing (" + String.format("%.4f", lineOf(desk, bill).bookedRate()) + ")",
                lineOf(desk, bill).coupon() == 0 && lineOf(desk, bill).bookedRate() > 0);
        check("a line's page is its line: BookDesk.line() is the desk's line for the piece", BookDesk.line(dm, term).equals(termLine));

        // A point on the curve.
        pointVsHigher.report("what a point takes off a line is what the curve built a point higher takes off its payments, to compounding's second order (2%; the worst here "
                + String.format("%.2f%%", pointVsHigher.worst * 100) + ")", .02);
        check("...a callable line's is on its schedule as a bond that cannot be called: it is the same measure, and it is something (" + String.format("%,.2f", lineOf(desk, call).valueOfAPoint()) + ")",
                lineOf(desk, call).valueOfAPoint() > 0);
        point.report("the line's value of a point is the ledger's valueOfAPoint()", 1e-9);
        dur.report("...its duration the value of a point over its worth and the point (the finite difference)", 1e-12);
        for (BookDesk.Line l : desk.lines()) {
            Debt d = l.debt();
            double life = d.getRemainingMonths() / 12.0;
            if (d instanceof FloatingRateNote) continue;
            check("a " + d.getType() + " line's duration is under its life and not by much (" + String.format("%.2f of %.2f years", l.duration(), life) + ")",
                    l.duration() > 0 && l.duration() < life + 1e-9 && (!(d instanceof LongTermBond) || d.getRemainingMonths() <= 12 || l.duration() > .5 * life));
        }
        check("a ten-year line's is between six tenths of its life and its life (" + String.format("%.2f of %.2f years", termLine.duration(), term.getRemainingMonths() / 12.0) + ")",
                termLine.duration() > .6 * term.getRemainingMonths() / 12.0 && termLine.duration() < term.getRemainingMonths() / 12.0);
        double oneMonth = 1.0 / 12;
        check("a floater's duration is under two months: its coupons follow the dial, so only the month to its next reset is at risk ("
                + String.format("%.3f years against a month's %.3f", floatLine.duration(), oneMonth) + ")",
                floatLine.duration() > 0 && floatLine.duration() < 2 * oneMonth);
        check("...and a point takes off it about its worth over twelve of them: within a quarter of a month's",
                Math.abs(floatLine.valueOfAPoint() - floatLine.worth() * DebtManager.A_POINT * oneMonth) <= .25 * floatLine.worth() * DebtManager.A_POINT * oneMonth);
    }

    /* =====================================================================
       5. A LINE'S NEXT SIX MONTHS ON THE BOOKS
       ===================================================================== */

    static void booksAhead() {
        out.println("\n--- a line's next six months on the books are the months it then books ---");

        Game g = FloatLinkCheck.readyCity("desk-books", true, 0);
        DebtManager dm = g.getDebtManager();
        double l0 = dm.priceIndexLevel();
        double[] rising = new double[14];
        Arrays.fill(rising, 1.01);
        FloatLinkCheck.Index idx = new FloatLinkCheck.Index(l0, rising);
        dm.setPriceIndexSource(idx);
        int month = g.getMonth();

        // Sold under par (opened at 92 per 100), an amortizing bond whose second month is an anniversary (38 months: 36 left after two), a bill, a line that was never sold, a floater and a linker.
        LongTermBond line = (LongTermBond) dm.addLongTermBond(20_000, 120, month, .05);
        line.markIssued(20_000 * .92, .058);
        line.settled();
        MediumTermBond serial = (MediumTermBond) dm.addMediumTermBond(12_000, 38, month, .04);
        serial.markIssued(12_000 * .97, .045);
        serial.settled();
        ShortTermTBill bill = (ShortTermTBill) dm.addShortTermTBill(5_000, 3, month);
        bill.markIssued(5_000 * .99, .04);
        bill.settled();
        LongTermBond unsold = (LongTermBond) dm.addLongTermBond(6_000, 60, month, .06);
        FloatingRateNote floater = FloatLinkCheck.fixtureFloater(g, 8_000, 9, .004);
        InflationLinkedBond linker = FloatLinkCheck.fixtureLinker(g, 10_000, 7, .02, l0);
        Debt[] pieces = {line, serial, bill, unsold, floater, linker};
        String[] names = {"the line sold under par", "the amortizing bond", "the bill", "the line never sold", "the floater", "the linker"};
        check("fixture: the line was sold under par: it is carried at less than its face (" + String.format("%,.0f of %,.0f", line.getCarryingValue(), line.getFaceValue()) + ")",
                line.getCarryingValue() < line.getFaceValue() && line.getEffectiveRate() > line.couponRate());

        // A piece whose books were never opened: the projection is read without opening them, at the coupon's rate on par.
        check("fixture: the line never sold has its books unopened", !unsold.booksOpen);
        java.util.List<Debt.BooksMonth> unsoldRows = unsold.booksAhead(1);
        Debt.BooksMonth u = unsoldRows.get(0);
        near("a line never sold: its first month is par carried, and the interest the coupon's, a twelfth of 6%", u.interest(), 6_000 * .06 / 12, 1e-9);
        check("...at par, with nothing wearing off, and the principal not yet repaid, and it left the books closed",
                u.opening() == 6_000 && Math.abs(u.amortised()) <= 1e-9 && u.repaid() == 0 && !unsold.booksOpen);

        // One month, the index lifting the linker's ratio: from here on it is held flat.
        idx.k = 3;
        press(g);
        check("fixture: the linker's ratio is over 1 and the bill has two months left (" + String.format("%.4f", linker.getRatio()) + ")",
                linker.getRatio() > 1 && bill.getRemainingMonths() == 2);

        java.util.List<java.util.List<Debt.BooksMonth>> rows = new ArrayList<>();
        double[] carried = new double[pieces.length];
        for (int p = 0; p < pieces.length; p++) {
            double c0 = pieces[p].getCarryingValue(), e0 = pieces[p].getEffectiveRate();
            rows.add(pieces[p].booksAhead(6));
            check(names[p] + ": reading the books ahead books nothing (carrying value and effective rate unmoved)",
                    pieces[p].getCarryingValue() == c0 && pieces[p].getEffectiveRate() == e0);
            carried[p] = c0;
        }
        check("the rows are as many as asked, or as many months as the piece has left: six, six, the bill's two, six, six, six",
                rows.get(0).size() == 6 && rows.get(1).size() == 6 && rows.get(2).size() == 2 && rows.get(3).size() == 6
                        && rows.get(4).size() == 6 && rows.get(5).size() == 6 && pieces[0].booksAhead(0).isEmpty());
        Debt.BooksMonth slice = rows.get(1).get(0);
        check("the amortizing bond's first month is the anniversary: the slice repaid is a third of its face (" + String.format("%,.2f", slice.repaid()) + ")",
                Math.abs(slice.repaid() - 12_000.0 / 3) <= 1e-6 && rows.get(1).get(1).repaid() == 0);
        Debt.BooksMonth billLast = rows.get(2).get(1);
        near("the bill's last row closes to nothing", billLast.closing(), 0, 0);
        near("...and repays its face, with no coupon", billLast.repaid(), 5_000, 1e-9);
        near("...the interest of that month what closes it: the payment less what it is carried at", billLast.interest(), 5_000 - billLast.opening(), 1e-9);
        double upliftWant = linker.getFaceValue() * (linker.getRatio() - 1);
        check("the linker's last row carries its inflation uplift in the coupon, as the press pays it (" + String.format("%,.2f", rows.get(5).get(5).coupon() - rows.get(5).get(4).coupon()) + " over a month's)",
                Math.abs(rows.get(5).get(5).coupon() - rows.get(5).get(4).coupon() - upliftWant) <= 1e-6 && upliftWant > 0);
        Worst foot = new Worst();
        for (int p = 0; p < pieces.length; p++) {
            java.util.List<Debt.BooksMonth> r = rows.get(p);
            for (int k = 0; k < r.size(); k++) {
                Debt.BooksMonth b = r.get(k);
                boolean last = k == r.size() - 1 && pieces[p].getRemainingMonths() == r.size();
                foot.add(last ? b.closing() : b.opening() + b.interest() - b.coupon() - b.repaid(), last ? 0 : b.closing(), names[p] + " month " + b.ahead());
            }
        }
        foot.report("every row foots: it closes at its opening and its interest less what the month pays, and the last month at nothing", 1e-9);

        // Six presses: each month's books are the row.
        Worst interest = new Worst(), coupon = new Worst(), amortised = new Worst(), closing = new Worst(), opening = new Worst();
        boolean firstCouponIsThePressCoupon = true;
        double serialPrincipal = serial.getOustandingPrincipal();
        double serialDrop = Double.NaN;
        for (int k = 1; k <= 6; k++) {
            for (Debt p : pieces) {
                if (p.getRemainingMonths() >= 1) firstCouponIsThePressCoupon &= p.couponScheduleOwed()[0] == p.couponInCurrency();
            }
            press(g);
            if (k == 1) serialDrop = serialPrincipal - serial.getOustandingPrincipal();
            for (int p = 0; p < pieces.length; p++) {
                if (rows.get(p).size() < k) continue;
                Debt.BooksMonth b = rows.get(p).get(k - 1);
                String at = names[p] + " month " + k;
                opening.add(b.opening(), carried[p], at);
                interest.add(pieces[p].getLastInterestExpense(), b.interest(), at);
                coupon.add(pieces[p].getLastCoupon(), b.coupon(), at);
                amortised.add(pieces[p].getLastAmortised(), b.amortised(), at);
                closing.add(pieces[p].getCarryingValue(), b.closing(), at);
                carried[p] = pieces[p].getCarryingValue();
            }
        }
        check("fixture: the bill matured and went off the books, and the linker's last month was pressed with the rest",
                bill.isMatured() && !dm.getDebt().contains(bill) && linker.isMatured() && !dm.getDebt().contains(linker));
        check("each month, before the press, the first coupon of the schedule is the coupon the press books, to the bit (the linker's last month's uplift included)",
                firstCouponIsThePressCoupon);
        opening.report("each row opens at what the piece was carried at when the month began", 0.005);
        interest.report("...the interest it books is the row's", 0.005);
        coupon.report("...the coupon it pays", 0.005);
        amortised.report("...the discount that wears off", 0.005);
        closing.report("...and what it is carried at after the month", 0.005);
        near("the amortizing bond repaid, in the month the row said, the slice the row said", serialDrop, slice.repaid(), 1e-6);
        near("the bill is carried at nothing after its last month", bill.getCarryingValue(), 0, 0);
    }

    /* =====================================================================
       6. A LINE REMEMBERS ITS PRICE AND ITS SALES
       ===================================================================== */

    /** What a sale holds, for comparing a piece with itself before and after. */
    static String saleText(Debt.Sale s) {
        return s.getMonth() + "|" + s.getFace() + "|" + s.getProceeds() + "|" + s.getYield() + "|" + s.getHow() + "|" + s.getRate();
    }

    static java.util.List<String> salesText(Debt d) {
        java.util.List<String> text = new ArrayList<>();
        for (Debt.Sale s : d.getSales()) text.add(saleText(s));
        return text;
    }

    static void remembers() throws Exception {
        out.println("\n--- a line remembers its price and its sales ---");

        Game g = FloatLinkCheck.readyCity("desk-memory", true, 0);
        DebtManager dm = g.getDebtManager();
        FloatLinkCheck.worldWantsPaper(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double ask = Math.max(100, g.getHouseholdBalance().spareForPaper() * 3);
        Debt line = sold(g, "a ten-year line", () -> g.handleLongBondLogic(ask, 10, 100));
        if (line == null) return;

        // The first sale.
        Debt.Sale first = line.getSales().isEmpty() ? null : line.getSales().get(0);
        check("the sale into the line is remembered: one sale, made in the month the line started, at auction",
                line.getSales().size() == 1 && first != null && first.getMonth() == line.getMonthStarted() && "auction".equals(first.getHow()));
        if (first == null) return;
        near("...of the face sold", first.getFace(), line.getFaceValue(), 1e-9);
        near("...for what it raised, which is what the books opened the line at", first.getProceeds(), line.getCarryingValue(), 1e-9);
        near("...at the yield it cleared at", first.getYield(), line.getIssueYield(), 0);
        check("before the month closes the line has no price path", line.getPricePath().length == 0 && line.getCarriedPath().length == 0);

        // Four months on: one entry a month, each the price and the carrying value per 100 that the harness read at that month's close.
        java.util.List<Double> wantPrice = new ArrayList<>(), wantCarried = new ArrayList<>();
        int firstClose = -1;
        for (int i = 0; i < 4; i++) {
            press(g);
            if (i == 0) firstClose = g.getMonth();
            wantPrice.add(dm.priceOfPar(line));
            wantCarried.add(line.getCarryingValue() / line.getOustandingPrincipal() * 100);
        }
        double[] price = line.getPricePath(), carr = line.getCarriedPath();
        check("four months after the sale the path has four entries, from the month after the sale (" + line.getPathFirstMonth() + ")",
                price.length == 4 && carr.length == 4 && line.getPathFirstMonth() == firstClose && firstClose == line.getMonthStarted() + 1);
        Worst pw = new Worst(), cw = new Worst();
        for (int i = 0; i < price.length; i++) {
            pw.add(price[i], wantPrice.get(i), "month " + i);
            cw.add(carr[i], wantCarried.get(i), "month " + i);
        }
        pw.report("...each entry is the price per 100 of what is owed that the harness read at that month's close", 1e-9);
        cw.report("...and the carrying value per 100", 1e-9);
        near("the last entry is where the line trades now, just after the press", price[3], dm.priceOfPar(line), 1e-9);
        near("...and what it is carried at now, per 100 of what is owed", carr[3], line.getCarryingValue() / line.getOustandingPrincipal() * 100, 1e-9);
        double[] copy = line.getPricePath();
        copy[0] = -1;
        check("the path is read as a copy: changing what it gave changes nothing", line.getPricePath()[0] == price[0]);

        // A reopening is one line with two sales.
        int pieces = dm.getDebt().size();
        double faceBefore = line.getFaceValue(), carriedBefore = line.getCarryingValue();
        int reopenMonth = g.getMonth();
        g.setCashForTest(Founding.WEALTHY_CASH);
        quietly(() -> g.handleLongBondLogic(ask, 10, 100));
        check("fixture: the second sale reopened the line: one line, and more face on it (" + String.format("%,.0f to %,.0f", faceBefore, line.getFaceValue()) + ")",
                dm.getDebt().size() == pieces && line.getFaceValue() > faceBefore + 1e-6);
        check("a reopening adds a sale and keeps one line: two sales, the first as it was", line.getSales().size() == 2 && saleText(line.getSales().get(0)).equals(saleText(first)));
        Debt.Sale second = line.getSales().size() > 1 ? line.getSales().get(1) : first;
        check("...the second made in the month of the reopening, at auction", second.getMonth() == reopenMonth && "auction".equals(second.getHow()));
        near("...for the face it added", second.getFace(), line.getFaceValue() - faceBefore, 1e-6);
        near("...for what it raised, which is what the line's carrying value grew by", second.getProceeds(), line.getCarryingValue() - carriedBefore, 1e-6);
        check("...and the path goes on as it was until the month closes", line.getPricePath().length == 4);

        // The rates rise and the month closes: a fifth entry, trading under what it is carried at.
        dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .05));
        g.strikeTheCurve();
        press(g);
        check("a fifth month, the rates up: a fifth entry, and the line trades under what it is carried at ("
                + String.format("%.2f against %.2f per 100", line.getPricePath()[4], line.getCarriedPath()[4]) + ")",
                line.getPricePath().length == 5 && line.getPricePath()[4] < line.getCarriedPath()[4]);

        // Part of it bought back: the sales are what was sold, the price is per 100.
        java.util.List<String> salesBefore = salesText(line);
        double[] pathBefore = line.getPricePath(), carriedPathBefore = line.getCarriedPath();
        g.setCashForTest(Founding.WEALTHY_CASH);
        double principal = line.getOustandingPrincipal();
        Buyback.Result deal = g.buyBack(line, principal / 3, Double.NaN);
        check("fixture: a part of the line was bought back, and the line is still on the books", deal.takenFace() > 0 && line.getOustandingPrincipal() < principal && dm.getDebt().contains(line));
        check("a part bought back keeps the sales, as they were made", salesText(line).equals(salesBefore));
        check("...and the path, which is per 100", Arrays.equals(line.getPricePath(), pathBefore) && Arrays.equals(line.getCarriedPath(), carriedPathBefore)
                && line.getPathFirstMonth() == firstClose);

        // A change of unit, on a piece of its own: the money scales, the per-100 path and the yields do not.
        LongTermBond lone = new LongTermBond(1_000, 120, 7, .05);
        lone.markIssued(950, .053);
        lone.reopen(500, 480, .056, 9, "auction");
        lone.recordPath(8, 97.5, 95.1);
        lone.recordPath(9, 96.0, 95.4);
        double[] lonePrice = lone.getPricePath(), loneCarried = lone.getCarriedPath();
        java.util.List<Debt.Sale> loneBefore = new ArrayList<>(lone.getSales());
        double[] faces = loneBefore.stream().mapToDouble(Debt.Sale::getFace).toArray(), raised = loneBefore.stream().mapToDouble(Debt.Sale::getProceeds).toArray();
        double[] yields = loneBefore.stream().mapToDouble(Debt.Sale::getYield).toArray();
        double scale = .01;
        lone.redenominate(scale);
        Worst unit = new Worst();
        for (int i = 0; i < 2; i++) {
            unit.add(lone.getSales().get(i).getFace(), faces[i] * scale, "face " + i);
            unit.add(lone.getSales().get(i).getProceeds(), raised[i] * scale, "proceeds " + i);
        }
        unit.report("a change of unit by a hundred takes each sale's face and proceeds to a hundredth", 1e-12);
        check("...but not the yields, the months or the words", lone.getSales().get(0).getYield() == yields[0] && lone.getSales().get(1).getYield() == yields[1]
                && lone.getSales().get(0).getMonth() == 7 && lone.getSales().get(1).getMonth() == 9 && "auction".equals(lone.getSales().get(1).getHow()));
        check("...nor the per-100 path", Arrays.equals(lone.getPricePath(), lonePrice) && Arrays.equals(lone.getCarriedPath(), loneCarried) && lone.getPathFirstMonth() == 8);

        // The central bank's sales, and the rollover's, as the piece is told them.
        LongTermBond bankTaken = new LongTermBond(1_000, 120, 7, .05);
        bankTaken.markIssued(950, .053, Debt.ROLLOVER);
        check("a sale the rollover made reads so", bankTaken.getSales().size() == 1 && "the rollover".equals(bankTaken.getSales().get(0).getHow()));
        bankTaken.addOnForCentralBank(100, 8);
        Debt.Sale addOn = bankTaken.getSales().get(1);
        check("the central bank's add-on is a sale of its own: its month, its face, and the words", addOn.getMonth() == 8 && addOn.getFace() == 100
                && "the central bank's add-on".equals(addOn.getHow()));
        near("...for what the sale it rides on raised per unit of face: 95 per 100", addOn.getProceeds(), 95, 1e-9);
        near("...at the yield of that sale", addOn.getYield(), .053, 0);
        bankTaken.reopenForCentralBank(200, 190, .054, 9);
        Debt.Sale par = bankTaken.getSales().get(2);
        check("the central bank's own par rolled into the line: its month, face and proceeds, and the words",
                par.getMonth() == 9 && par.getFace() == 200 && par.getProceeds() == 190 && par.getYield() == .054 && "the central bank, at its par".equals(par.getHow()));
        check("a piece built without a sale has none, and no path", new LongTermBond(1_000, 120, 7, .05).getSales().isEmpty()
                && new LongTermBond(1_000, 120, 7, .05).getPricePath().length == 0);

        // The treasury's rollover sells what falls due: the pieces it makes say so, and a sale at auction after it reads auction again.
        Game rg = FloatLinkCheck.readyCity("desk-rollover", false, 0);
        DebtManager rdm = rg.getDebtManager();
        rg.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        FloatLinkCheck.dueNextMonth(FloatLinkCheck.fixtureFloater(rg, 20_000, 24, .002));
        FloatLinkCheck.dueNextMonth(FloatLinkCheck.fixtureLinker(rg, 20_000, 120, .02, rdm.priceIndexLevel()));
        rg.setCashForTest(0);
        java.util.Set<Debt> had = FloatLinkCheck.piecesOf(rdm);
        press(rg);
        java.util.List<Debt> fresh = new ArrayList<>();
        for (Debt d : rdm.getDebt()) if (!had.contains(d)) fresh.add(d);
        check("fixture: the rollover sold new pieces for the two that fell due (" + fresh.size() + ")", fresh.size() == 2);
        check("a sale the treasury's rollover made reads so on the piece it made: one sale each, in the month it was made",
                fresh.stream().allMatch(d -> d.getSales().size() == 1 && "the rollover".equals(d.getSales().get(0).getHow())
                        && d.getSales().get(0).getMonth() == d.getMonthStarted()));
        rg.setCashForTest(Founding.WEALTHY_CASH);
        double rask = FloatLinkCheck.askOf(rg, .1);
        Debt afterwards = sold(rg, "a three-month bill after the rollover", () -> rg.handleTBillLogic(rask, 3, 100));
        check("...and a sale the player makes after it reads auction", afterwards != null && afterwards.getSales().size() == 1 && "auction".equals(afterwards.getSales().get(0).getHow()));

        // The path's own rules: a figure that is no number, a month recorded twice, a month skipped.
        LongTermBond rec = new LongTermBond(1_000, 120, 5, .05);
        rec.recordPath(5, Double.NaN, Double.NaN);
        check("a first entry that is no number is 100, and the path starts at the month it was first recorded", rec.getPricePath()[0] == 100 && rec.getCarriedPath()[0] == 100 && rec.getPathFirstMonth() == 5);
        rec.recordPath(6, 98, 97);
        rec.recordPath(7, Double.POSITIVE_INFINITY, 96.5);
        check("a later entry that is no number is the entry before it", rec.getPricePath()[2] == 98 && rec.getCarriedPath()[2] == 96.5);
        rec.recordPath(7, 99, 96.5);
        check("a month recorded again is overwritten, not added", rec.getPricePath().length == 3 && rec.getPricePath()[2] == 99);
        rec.recordPath(10, 95, 96);
        check("a month skipped repeats the entry before it, and an earlier month adds nothing",
                rec.getPricePath().length == 6 && rec.getPricePath()[3] == 99 && rec.getPricePath()[4] == 99 && rec.getPricePath()[5] == 95
                        && recordedEarlier(rec));

        // Saved and loaded.
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = g.saveGame(10, "desk").ok);
        check("fixture: the city saved to slot 10 of its scratch root", ok[0]);
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        int at = dm.getDebt().indexOf(line);
        Debt loaded = at >= 0 && at < back.getDebtManager().getDebt().size() ? back.getDebtManager().getDebt().get(at) : null;
        check("fixture: the loaded city has the line", loaded != null && loaded.getMonthStarted() == line.getMonthStarted()
                && loaded.getOustandingPrincipal() == line.getOustandingPrincipal());
        if (loaded == null) return;
        check("saved and loaded, the sales are the same", salesText(loaded).equals(salesText(line)) && loaded.getSales().size() == 2);
        check("...the path is the same, from the same month", Arrays.equals(loaded.getPricePath(), line.getPricePath())
                && Arrays.equals(loaded.getCarriedPath(), line.getCarriedPath()) && loaded.getPathFirstMonth() == line.getPathFirstMonth());
        press(g);
        press(back);
        check("the next month adds one entry on each: the original's and the loaded city's (6 each)", line.getPricePath().length == 6 && loaded.getPricePath().length == 6
                && line.getCarriedPath().length == 6 && loaded.getCarriedPath().length == 6);
        near("...the loaded city's is where its line trades after the press", loaded.getPricePath()[5], back.getDebtManager().priceOfPar(loaded), 1e-9);
        check("...and the sales are still two on each", line.getSales().size() == 2 && loaded.getSales().size() == 2);
    }

    /** A month earlier than the path's last adds nothing to it. */
    static boolean recordedEarlier(Debt d) {
        int n = d.getPricePath().length;
        d.recordPath(d.getPathFirstMonth() + n - 3, 1, 1);
        return d.getPricePath().length == n && d.getPricePath()[n - 1] == 95;
    }

    /* =====================================================================
       7. THE CURVE'S PAST
       ===================================================================== */

    /** The city's zero rates at the curve's tenors, read now. */
    static double[] tenorsNow(DebtManager dm) {
        double[] now = new double[DebtManager.CURVE_TENORS.length];
        for (int i = 0; i < now.length; i++) now[i] = dm.curveRate(DebtManager.CURVE_TENORS[i]);
        return now;
    }

    static double[] worldNow(DebtManager dm) {
        double[] now = new double[DebtManager.CURVE_TENORS.length];
        for (int i = 0; i < now.length; i++) now[i] = dm.foreignCurveRate(DebtManager.CURVE_TENORS[i]);
        return now;
    }

    /** A played city with a paper of every kind a sale can make, the book recording or not (a twin's). */
    static Game twin(String label, boolean recording) {
        Game g = FloatLinkCheck.readyCity(label, true, 0);
        g.getDebtManager().setRecordingForTest(recording);
        FloatLinkCheck.worldWantsPaper(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        double ask = FloatLinkCheck.askOf(g, .1);
        quietly(() -> {
            g.handleTBillLogic(ask, 3, 100);
            g.handleMediumBondLogic(ask, 5, 100);
            g.handleLongBondLogic(ask, 10, 100);
            g.handleLongBondLogic(ask, 10, 100, Auction.Cap.DEFAULT, true);
            g.handleFloatingNoteLogic(ask, 2, 100);
            g.handleLinkedBondLogic(ask, 10, 100);
        });
        return g;
    }

    /** The figures the month computes, for holding one city to its twin's. */
    static double[] fingerprint(Game g) {
        DebtManager dm = g.getDebtManager();
        return new double[] {g.getCash(), dm.getRate(), dm.getPolicyRate(), dm.getAllPrincipal(), dm.getCarryingValue(), dm.getInterestBooked(),
                dm.getTotalMarketValue(), g.getEconomyManager().getNationalAccounts().getInterestExpense()};
    }

    static void theCurvesPast() throws Exception {
        out.println("\n--- the curve's past ---");

        Game a = twin("desk-twin-a", true);
        Game b = twin("desk-twin-b", false);
        DebtManager adm = a.getDebtManager(), bdm = b.getDebtManager();
        check("fixture: the twins are the same city before the first press",
                Arrays.equals(fingerprint(a), fingerprint(b)) && adm.getDebt().size() == bdm.getDebt().size());
        boolean callable = adm.getDebt().stream().anyMatch(d -> d instanceof LongTermBond bond && bond.isCallable());
        check("fixture: ...with a bill, an amortizing bond, a line, a callable line, a floater and a linker on the books ("
                        + adm.getDebt().stream().map(Debt::getType).collect(java.util.stream.Collectors.joining(" ")) + (callable ? ", one callable" : "") + ")",
                onBooks(adm, "NOTE") >= 1 && onBooks(adm, "SERIAL") >= 1 && onBooks(adm, "TERM") >= 2 && onBooks(adm, "FLOAT") >= 1 && onBooks(adm, "LINKED") >= 1 && callable);

        int months = 26;
        // The played city recorded its own setup, with the recording on: the twin's book at the start is that.
        double[][] bookBeforeB = bdm.curveBookToSave();
        double[][] kept = new double[months + 1][], keptWorld = new double[months + 1][];
        double[] keptDial = new double[months + 1];
        boolean sameThroughout = true, auditsClose = true;
        int firstDifference = -1;
        for (int i = 1; i <= months; i++) {
            // The dial moves partway through, in both cities: up, then down.
            if (i == 9) { quietly(() -> adm.takeTheDial(.07)); quietly(() -> bdm.takeTheDial(.07)); }
            if (i == 17) { quietly(() -> adm.takeTheDial(.02)); quietly(() -> bdm.takeTheDial(.02)); }
            press(a);
            press(b);
            kept[i] = tenorsNow(adm);
            keptWorld[i] = worldNow(adm);
            keptDial[i] = adm.getPolicyRate();
            boolean same = Arrays.equals(fingerprint(a), fingerprint(b));
            if (!same && firstDifference < 0) firstDifference = i;
            sameThroughout &= same;
            auditsClose &= FloatLinkCheck.closes(a.getLastMoneyAudit()) && FloatLinkCheck.closes(b.getLastMoneyAudit());
        }
        check("TRACE GUARD: the city that records and its twin that does not play " + months + " months alike - cash, the rate, the dial, the debt, what it is carried at, "
                + "the interest booked, the paper's market value and the budget's interest line, to the bit" + (firstDifference > 0 ? " (they part at month " + firstDifference + ")" : ""),
                sameThroughout);
        check("...and both months' audits close in every month", auditsClose);
        double[][] bookA = adm.curveBookToSave();
        check("fixture: the twin that does not record kept no month of the " + months + " (its book is the setup's, as it was, and "
                        + bdm.getDebt().stream().filter(d -> d.getPricePath().length > 0).count() + " of its pieces have a path)",
                Arrays.deepEquals(bdm.curveBookToSave(), bookBeforeB) && bookA[bookA.length - 1][0] == bookBeforeB[bookBeforeB.length - 1][0] + months
                        && bdm.getDebt().stream().allMatch(d -> d.getPricePath().length == 0));
        check("...and the one that does has a book and a path on its paper (" + adm.curveBookSize() + " months of curve, "
                        + adm.getDebt().stream().filter(d -> d.getPricePath().length > 0).count() + " of " + adm.getDebt().size() + " pieces with a path)",
                adm.curveBookSize() == DebtManager.CURVE_KEPT && adm.getDebt().stream().allMatch(d -> d.getPricePath().length >= 1));

        // What was recorded.
        double apart = 0;
        for (int j = 0; j < kept[months].length; j++) apart = Math.max(apart, Math.abs(kept[months - 12][j] - kept[months][j]));
        check("fixture: the dial moved, so the curve a year ago and the curve now differ (by up to " + String.format("%.4f", apart) + ")", apart > 1e-3);
        check("the curve book keeps the newest " + DebtManager.CURVE_KEPT + " months", adm.curveBookSize() == DebtManager.CURVE_KEPT);
        check("0 months ago is the curve now: the zero rates at the curve's tenors", Arrays.equals(adm.curveMonthsAgo(0), kept[months]) && Arrays.equals(adm.curveMonthsAgo(0), tenorsNow(adm)));
        check("12 months ago is the zero rates the harness read at that month's close", Arrays.equals(adm.curveMonthsAgo(12), kept[months - 12]));
        check("24 months ago is the oldest the book holds, and the harness read it then", Arrays.equals(adm.curveMonthsAgo(24), kept[months - 24]));
        check("25 months ago was not kept (the book keeps " + DebtManager.CURVE_KEPT + "), and a month ahead was never recorded", adm.curveMonthsAgo(DebtManager.CURVE_KEPT) == null && adm.curveMonthsAgo(-1) == null);
        check("the world's curve likewise: now, a year ago, two years ago, and none beyond",
                Arrays.equals(adm.worldCurveMonthsAgo(0), keptWorld[months]) && Arrays.equals(adm.worldCurveMonthsAgo(12), keptWorld[months - 12])
                        && Arrays.equals(adm.worldCurveMonthsAgo(24), keptWorld[months - 24]) && adm.worldCurveMonthsAgo(DebtManager.CURVE_KEPT) == null);
        check("...and the dial that month", adm.policyRateMonthsAgo(12) == keptDial[months - 12] && adm.policyRateMonthsAgo(24) == keptDial[months - 24]
                && Double.isNaN(adm.policyRateMonthsAgo(DebtManager.CURVE_KEPT)));
        check("the tenors are the curve's: three months to fifty years, in order", DebtManager.CURVE_TENORS.length == adm.curveMonthsAgo(0).length
                && DebtManager.CURVE_TENORS[0] == 3 && DebtManager.CURVE_TENORS[DebtManager.CURVE_TENORS.length - 1] == 600
                && Arrays.stream(DebtManager.CURVE_TENORS).sorted().toArray().length == DebtManager.CURVE_TENORS.length);
        double[] copy = adm.curveMonthsAgo(12);
        copy[0] = -1;
        check("the book is read as a copy", adm.curveMonthsAgo(12)[0] == kept[months - 12][0]);

        // The rule and the expected dial.
        check("fixture: the month struck the rule's inflation and the city's expectation", adm.isStruck());
        near("the rule at today's inflation is the rule's rate at the inflation the month struck", adm.ruleNow(), adm.advisedPolicyRate(adm.getStruckInflation()), 0);
        double dial = Math.min(DebtManager.MAX_POLICY_RATE, adm.ruleNow() + .02);
        quietly(() -> adm.takeTheDial(dial));
        double[] path = adm.expectedPolicyPath();
        near("the dial the market expects starts at the dial", path[0], adm.getPolicyRate(), 0);
        double gap = adm.getPolicyRate() - adm.ruleNow();
        check("fixture: the dial is off the rule, by " + String.format("%.4f", gap), Math.abs(gap) > 1e-4);
        check("...so the path's first month is the dial and not the rule's rate at today's inflation, and the gap between them closes: the far end is where the dial settles",
                path[0] != adm.ruleNow() && Math.abs(path[YieldCurve.HORIZON - 1] - adm.settledDial()) <= 1e-6);
        quietly(() -> adm.takeTheDial(adm.ruleNow()));
        near("with the dial on the rule, the path's first month is the rule's rate at today's inflation", adm.expectedPolicyPath()[0], adm.ruleNow(), 1e-12);
        double[] settled = adm.expectedPolicyPath();
        near("the dial settles where the rule is at the inflation the city expects to settle at", settled[YieldCurve.HORIZON - 1], adm.settledDial(), 1e-6);
        near("...which is the rule's rate at the expectation the month struck", adm.settledDial(), adm.advisedPolicyRate(adm.getStruckExpected()), 0);

        // Your rate's other figures (slice D): the nominal par yield the linked line is set against, read off the same
        // curve as the real coupon and the breakeven; and the ten-year rate, taken apart into the four parts the page draws.
        for (int m : new int[] {120, 360}) {
            near("the nominal par yield at " + m + " months is the real coupon a linked bond sells at plus the inflation it prices",
                    adm.parYield(m, 0), adm.fairRealCoupon(m, 0) + adm.breakevenInflation(m, 0), 1e-12);
        }
        near("the ten-year zero rate is its four parts: the dial expected over ten years, the debt's spread, the term premium, less the central bank's compression",
                adm.curveRate(120), adm.expectedDialOver(120) + adm.creditSpread() + DebtManager.termPremium(120) - adm.compression(120), 1e-9);

        // Saved and loaded.
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = a.saveGame(10, "desk").ok);
        check("fixture: the city saved to slot 10 of its scratch root", ok[0]);
        Game back = new Game(a.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        DebtManager loaded = back.getDebtManager();
        check("saved and loaded, the curve book is the same: a year ago, two years ago, the world's, the dial, and nothing beyond",
                loaded.curveBookSize() == adm.curveBookSize() && Arrays.equals(loaded.curveMonthsAgo(12), adm.curveMonthsAgo(12))
                        && Arrays.equals(loaded.curveMonthsAgo(24), adm.curveMonthsAgo(24)) && Arrays.equals(loaded.curveMonthsAgo(0), adm.curveMonthsAgo(0))
                        && Arrays.equals(loaded.worldCurveMonthsAgo(12), adm.worldCurveMonthsAgo(12)) && loaded.policyRateMonthsAgo(24) == adm.policyRateMonthsAgo(24)
                        && loaded.curveMonthsAgo(DebtManager.CURVE_KEPT) == null);
        double[][] saved = loaded.curveBookToSave();
        loaded.restoreCurveBook(null);
        check("a save from before the book has none: the book is empty and reads as no month kept", loaded.curveBookSize() == 0 && loaded.curveMonthsAgo(0) == null);
        loaded.restoreCurveBook(new double[][] {{1, 2, 3}});
        check("...and so is a book of rows the wrong length (a changed tenor list)", loaded.curveBookSize() == 0);
        loaded.restoreCurveBook(saved);
        check("...and the book put back is the book saved", Arrays.deepEquals(loaded.curveBookToSave(), saved));
        press(back);
        check("the next month's close adds a month to the loaded book, and drops the oldest",
                loaded.curveBookSize() == DebtManager.CURVE_KEPT && loaded.curveMonthsAgo(23) != null
                        && Arrays.equals(loaded.curveMonthsAgo(12), adm.curveMonthsAgo(11)) && loaded.curveMonthsAgo(DebtManager.CURVE_KEPT) == null);
    }

    /* =====================================================================
       8. WHAT CLOSING A LINE NOW WOULD BOOK, GREEN OR RED
       ===================================================================== */

    /** A bank-held two-year line sold at par at today's curve: its coupon the curve's two-year par yield, carried at its face, settled. A short line, because the curve's short end follows the dial and its long end mostly does not. */
    static LongTermBond bankHeldAtPar(Game g, double face) {
        final int months = 24;
        DebtManager dm = g.getDebtManager();
        double coupon = dm.curve().parYield(months);
        LongTermBond line = (LongTermBond) dm.addLongTermBond(face, months, g.getMonth(), coupon);
        line.markIssued(face, coupon);
        line.settled();
        return line;
    }

    static void whatClosingBooks() {
        out.println("\n--- what closing a line now would book, green or red, is what the buyback books ---");

        Game g = FloatLinkCheck.readyCity("desk-close", true, 0);
        DebtManager dm = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        ForeignAccounts fx = g.getForeignAccounts();
        fx.pinRate(1.25);
        press(g);
        FloatLinkCheck.worldWantsPaper(g);
        g.setCashForTest(Founding.WEALTHY_CASH);

        // The dollar line, sold at the D$ of its day, 1.25.
        Debt usd = sold(g, "a five-year dollar bond", () -> g.handleForeignLogic("Term", 5_000, 5, 100, false));
        // The gain line: sold at par at today's rates (the dial at 3%), then the dial raised five points, so it trades under what it is carried at.
        LongTermBond gain = bankHeldAtPar(g, 20_000);
        dm.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, dm.getPolicyRate() + .05));
        g.strikeTheCurve();
        press(g);
        // The loss line: sold at par at the higher rates, then the dial cut four points, so it trades over what it is carried at (the first line, sold lower, is still under).
        LongTermBond loss = bankHeldAtPar(g, 20_000);
        dm.setPolicyRate(Math.max(DebtManager.MIN_POLICY_RATE, dm.getPolicyRate() - .04));
        g.strikeTheCurve();
        press(g);
        // The D$ moves after the dollar line was sold, and the month ends on the new rate: the dollar line is valued at the month's actual D$.
        fx.pinRate(1.4);
        press(g);
        g.setCashForTest(Founding.WEALTHY_CASH);
        if (usd == null) return;

        BookDesk desk = g.bookDesk();
        BookDesk.Line gl = lineOf(desk, gain), ll = lineOf(desk, loss), ul = lineOf(desk, usd);
        out.printf("   the gain line: worth %,.1f carried %,.1f, closed now %+,.1f (%+.2f%%); the loss line: worth %,.1f carried %,.1f, closed now %+,.1f (%+.2f%%); the dollar line: %+,.1f (%+.2f%%)%n",
                gl.worth(), gl.carried(), gl.ifClosedNow(), gl.ifClosedNowShare() * 100, ll.worth(), ll.carried(), ll.ifClosedNow(), ll.ifClosedNowShare() * 100,
                ul.ifClosedNow(), ul.ifClosedNowShare() * 100);
        check("fixture: the rates rose after the first line was sold, so it trades under what it is carried at",
                gl.worth() < gl.carried() && gl.pricePer100() < gl.carriedPer100());
        check("fixture: the rates fell after the second was sold, so it trades over what it is carried at",
                ll.worth() > ll.carried() && ll.pricePer100() > ll.carriedPer100());
        // Why these are the fixture: the bank offers at fair (D34), so the reverse auction pays exactly fair for a line the bank alone holds. The households' and the
        // businesses' slices ask more than fair, and would put the auction's price over the figure the desk gives; a line sold at auction has some of both.
        check("fixture: the households, the businesses, the world and the central bank hold none of either line: the bank holds it alone, and offers at fair",
                gain.bankPrincipal() == gain.getOustandingPrincipal() && loss.bankPrincipal() == loss.getOustandingPrincipal()
                        && gain.householdPrincipal() + gain.firmPrincipal() + gain.worldPrincipal() + gain.centralBankPrincipal()
                        + loss.householdPrincipal() + loss.firmPrincipal() + loss.worldPrincipal() + loss.centralBankPrincipal() == 0);
        check("fixture: the dollar line is held by the world alone, which offers the whole piece at fair (D26)", usd.isForeign() && ul.world() == 1);

        // The line's gain, as a figure and as a share of what the line is carried at.
        check("the gain line's figure is above nothing, and its share (" + String.format("%.2f%%", gl.ifClosedNowShare() * 100) + ")",
                gl.ifClosedNow() > 0 && gl.ifClosedNowShare() > 0);
        check("the loss line's figure is below nothing, and its share (" + String.format("%.2f%%", ll.ifClosedNowShare() * 100) + ")",
                ll.ifClosedNow() < 0 && ll.ifClosedNowShare() < 0);
        check("the dollar line has a gain or a loss to read, not a nothing (" + String.format("%+,.3f", ul.ifClosedNow()) + ")", Math.abs(ul.ifClosedNow()) > 1e-6);
        for (BookDesk.Line l : desk.lines()) {
            near("a line's share is its gain over its carrying value (" + l.debt().getType() + "/" + l.monthsLeft() + ")", l.ifClosedNowShare(), l.ifClosedNow() / l.carried(), 1e-12);
        }

        // The book's total: the lines' gains, over the lines' carrying values.
        double sumGain = 0, sumCarried = 0;
        for (BookDesk.Line l : desk.lines()) {
            sumGain += l.ifClosedNow();
            sumCarried += l.carried();
        }
        near("the book's gain is the lines' gains added up", desk.ifClosedNow(), sumGain, 1e-6);
        near("the book's share is the total gain over the total carrying value", desk.ifClosedNowShare(), sumGain / sumCarried, 1e-9);
        near("...which is the ledger's carrying value less its market value, over the ledger's carrying value",
                desk.ifClosedNowShare(), (dm.getCarryingValue() - dm.getTotalMarketValue()) / dm.getCarryingValue(), 1e-9);
        check("...and a book carried at nothing has a share of nothing, not a division by nothing", BookDesk.of(new DebtManager()).ifClosedNowShare() == 0);

        // The dollar line is valued at the month's actual D$, worked out here from its own dollars.
        double rate = fx.getRate();
        check("the month ended on the D$ it was pinned at (" + rate + "), and the dollar piece and the ledger are valued at it",
                rate == 1.4 && usd.getExchangeRate() == rate && dm.getExchangeRate() == rate);
        double[] flowsUsd = usd.remainingCashFlows();
        for (int i = 0; i < flowsUsd.length; i++) flowsUsd[i] /= rate;
        double worthUsd = dm.worldCurveAt(dm.foreignRate()).pv(flowsUsd);
        near("the dollar line's worth is its dollars' worth on the world's curve, at the month's D$ (worked out here)", ul.worth(), worthUsd * rate, 1e-6);
        near("...its carried is its dollars carried, at the month's D$", ul.carried(), usd.carryingInCurrency() * rate, 1e-6);
        near("...so its gain is the dollars' gain at the month's D$, not at the D$ it was sold at",
                ul.ifClosedNow(), (usd.carryingInCurrency() - worthUsd) * rate, 1e-6);
        check("...and that is not the figure at 1.25, the D$ of its day (it differs by " + String.format("%+,.3f", (usd.carryingInCurrency() - worthUsd) * (rate - 1.25)) + ")",
                Math.abs((usd.carryingInCurrency() - worthUsd) * (rate - 1.25)) > 1e-6);

        // Each line bought back whole, off a fresh read of the desk (the market moves as paper is retired): the books take the gain the desk gave.
        double booked = 0;
        for (Debt piece : new Debt[] {gain, loss, usd}) {
            String name = piece == gain ? "the gain line" : piece == loss ? "the loss line" : "the dollar line";
            BookDesk.Line before = lineOf(g.bookDesk(), piece);
            double carriedBefore = piece.getCarryingValue(), principal = piece.getOustandingPrincipal();
            g.setCashForTest(Founding.WEALTHY_CASH);
            Buyback.Result deal = g.buyBack(piece, 1e12, Double.NaN);
            check(name + " was bought back whole and is off the books", Math.abs(deal.takenFace() - principal) <= 1e-6 && !dm.getDebt().contains(piece));
            near("...it paid exactly its fair price (the one holder offers at fair)", deal.paid(), before.worth(), 1e-6);
            near("...the gain the buyback booked is the figure the desk gave before it", deal.gainOnCarrying(), before.ifClosedNow(), 1e-6);
            near("...and its share is the desk's share, and the gain over the carrying value read before", deal.gainOnCarryingShare(), before.ifClosedNowShare(), 1e-9);
            near("...which is the gain over what the line was carried at", before.ifClosedNowShare(), deal.gainOnCarrying() / carriedBefore, 1e-9);
            booked += deal.gainOnCarrying();
        }
        press(g);
        near("the month's gain on debt retired is the three lines' figures added up", na.getRetiredGain(), booked, 1e-6);
    }
}
