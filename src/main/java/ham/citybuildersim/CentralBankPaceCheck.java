package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Proves the central bank's pace on its holdings and its auction share (CBK2): the pace dial both ways, the runoff's caps,
 * sales at market with the gain or loss realised, the auction share at the auction's price and inside the dial, the money
 * audit over all of it, and the law that bars direct purchases. Not part of the game.
 *
 * WHY THIS EXISTS. Jerus, 2026-11-26: "setting it at 10% doesnt mean it goes to 10% right away, just means thats teh cap",
 * and "when you issue debt the ability to just issue directly to the central bank? if real life is not like that then no".
 * Until CBK2 the pace was QE_SPEED's, a quarter of the larger setting a month, never the player's; QT was whatever ran off
 * past the dial plus sales at that pace; and the bank bought at issue only its own maturing par. A pace that is wrong by a
 * tenth moves the book a tenth too fast every month and nothing else in the game would say so; a purchase at the wrong price
 * is money made that the audit sees and the bank's books do not agree with.
 *
 * WHAT THIS HAS TO PROVE, every one a fixture that CAUSES its condition on one town (a five-year serial the households, the
 * bank and the central bank each hold part of, saved a month before its first slice) loaded fresh for each case:
 *
 *   1. THE PACE BOTH WAYS. A set pace moves the holding up by exactly pace of the term paper in a month with nothing falling
 *      due, and down by exactly the pace in sale mode; Auto's step is QE_SPEED of the larger of the dial, the setting before
 *      it and the share held, to the bit, and a played month moves by it - or by what is left to the dial when that is nearer.
 *
 *   2. THE RUNOFF'S CAPS HOLD. Under runoff only nothing is sold; what runs off of its maturing par is the pace when the pace
 *      binds, all its par over the dial when the pace does not, and what it holds over its dial when that is the least.
 *
 *   3. SALES AT MARKET, GAINS AND LOSSES REALISED. What a sale is paid is the face given up at each piece's market value per
 *      face; the paper gains line is that price less the face - a loss with the rates up, a gain with them down.
 *
 *   4. AUCTION PURCHASES AT THE AUCTION'S PRICE AND INSIDE THE CAP. Under the law, the share of the face the market bought, at
 *      the issue's own price per face, pro rata over the pieces; with the dial near, no more than the dial's room; none at or
 *      over the dial; and the money made is on the money statement's MADE lines, closing.
 *
 *   5. WITH THE LAW OFF, NO NEW PAPER AT AUCTION beyond the rollover, whatever the share; and the law on with a share of
 *      nothing takes nothing.
 *
 *   6. CREDIBILITY. Lending the treasury at auction costs nothing up to the anchored share of a year's GDP and its full at
 *      the unanchored, in a straight line between; the full cost takes a FINANCING_LOSS_MONTHS-th of the way to KMIN a month
 *      and adds up to FINANCING_PREMIUM to expected inflation, bounded both ways over fifty years, as the cases measured. On
 *      twin towns, the one whose bank takes the market's face at auction is believed less, expects more inflation and has the
 *      weaker currency; when it stops, the stock is forgotten a twelfth a month and the trust comes back only at GAIN_MONTHS.
 *
 *   7. DEFAULTS AND SAVES. A new city's rules are Auto, runoff and sales, no share, the law off - the rules before CBK2 - and
 *      the fixture loads at them; every rule, the auction's lifetimes, the lending stock and the premium come back from a
 *      save to the bit; and a save from before CBK2 (no rules key, no premium, the bank's array without its three slots)
 *      reads the defaults and plays its next month to the bit as the same save with them.
 *
 *   8. THE MONEY AUDIT TO THE CENT, over every month any section played.
 */
public class CentralBankPaceCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** The scratch slot the fixture is saved in: the assistant's slot, in a scratch folder (never the player's 1-9). */
    static final int SLOT = 10;

    /** The pace the set-pace cases use: a hundredth of the term paper a month, small beside the .4 the fixture holds so that neither the dial nor the paper there is to buy or sell stops it. */
    static final double PACE = .01;

    /** The dial the buying cases raise it to: far above the holding, so that the pace and not the dial is what stops it. */
    static final double DIAL_UP = .9;

    /** The dial the selling cases lower it to: far below the holding, so that the pace and not the dial is what stops it. */
    static final double DIAL_DOWN = .1;

    /** How far above the holding the near-dial case puts the dial: nearer than the quarter of the larger setting Auto allows. */
    static final double DIAL_NEAR = .01;

    /** The auction share the cases give the central bank: a quarter of the face the market buys. */
    static final double SHARE = .25;

    /** Of the share's bid, the part the dial's room allows in the capped case: half, so that the cap binds with the bid unmet. */
    static final double ROOM_OF_BID = .5;

    /** How far the policy rate is put up at a time in the loss case, until the paper stands under face (CentralBankCheck puts it up four points for a bond sold at the new rate; this paper carries the old, higher coupon). */
    static final double RATES_UP = .04;

    /** The most steps the loss case puts the rate up before it gives up on the paper standing under face. */
    static final int MOST_RATE_STEPS = 10;

    /** How far under its face the paper must stand for the loss case: more than the month's own re-strike of the curve moves its price (0.2% where nothing else changed), so the sign holds through the window. */
    static final double UNDER_FACE = .01;

    /** How far the price of paper per unit of face can move between the window that prices the sale and the probe that reads it: the month's GDP and taxes reach the spread after the window (priceTheDebtMarket), and the sale lowers the share the compression follows; 3.4e-5 where measured, against 5e-2 for paper paid for at face. */
    static final double CURVE_DRIFT = 3e-4;

    /** The face of the new paper the cases sell between the presses, in households' purses: the fixture's own size (CentralBankCheck 19), so the market takes it. */
    static final double SALE_IN_PURSES = 3;

    /** ...and the least it is, which the treasury's lineup sells. */
    static final double SALE_FLOOR = 20_000;

    /** The most months any loop here plays before it gives up on the condition it plays to. */
    static final int MOST_MONTHS = 30;

    static GameFiles files;
    static int soldIn;

    static int monthsPlayed, monthsOff;
    static double worstResidual, worstRelative;

    /* ============================ the helpers ============================ */

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** actual against expected, to tol of whichever is larger (or of 1, for figures near nothing). */
    static void near(String label, double actual, double expected, double tol) {
        near(label, actual, expected, Math.max(1, Math.max(Math.abs(actual), Math.abs(expected))), tol);
    }

    /** actual against expected, to tol of a scale of the caller's: for a difference of two big figures. */
    static void near(String label, double actual, double expected, double scale, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol * scale;
        if (!ok) fails++;
        out.printf("%-96s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** A month played and held to the money audit: the identity of every month in every section (section 5). */
    static void play(Game g) {
        quietly(g::toggleNextMonth);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        boolean closes = Math.abs(r.residual) <= MoneyAudit.CENT || r.relative() <= 1e-7;
        monthsPlayed++;
        worstResidual = Math.max(worstResidual, Math.abs(r.residual));
        worstRelative = Math.max(worstRelative, r.relative());
        if (!closes) {
            monthsOff++;
            out.printf("   m%d: the money audit's residual %.6f, %.3e of what moved%n", g.getMonth(), r.residual, r.relative());
        }
    }

    /** One month played with a reading taken after the central bank's roll and its holdings step, before the households settle (Game.settleProbeForTest). */
    static void playProbed(Game g, Runnable atTheProbe) {
        g.settleProbeForTest = after -> { if (!after) atTheProbe.run(); };
        try { play(g); } finally { g.settleProbeForTest = null; }
    }

    /** The fixture as saved, loaded fresh and set to roll as the treasury of section 19 does. */
    static Game copy() {
        Game g = new Game(files);
        quietly(() -> g.loadGameSave(SLOT));
        g.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        return g;
    }

    /** True when no piece of the city's term paper has principal falling due next month: the premise of every case of pure pace. */
    static boolean nothingFallsDue(Game g) {
        for (Debt d : g.getDebtManager().getDebt()) {
            if (DebtManager.isTermPaper(d) && d.principalDueNextMonth() > 0) return false;
        }
        return true;
    }

    /** One piece of the city's term paper as a reading found it. */
    record Piece(Debt d, double cb, double face, double market, double settleDue, double unsettled) { }

    /** The central bank's holding, the term paper and its lines of the month, and each piece, as one reading found them. */
    record Snap(double held, double term, double sold, double gains, double parIssue, double boughtIssue,
                double parAuction, double boughtAuction, double issued, List<Piece> pieces) {
        Piece of(Debt d) {
            for (Piece p : pieces) if (p.d() == d) return p;
            return null;
        }
        double cbOf(Debt d) {
            Piece p = of(d);
            return p == null ? 0 : p.cb();
        }
    }

    static Snap snap(Game g) {
        CentralBank cb = g.getCentralBank();
        DebtManager dm = g.getDebtManager();
        List<Piece> pieces = new ArrayList<>();
        for (Debt d : dm.getDebt()) {
            if (!DebtManager.isTermPaper(d)) continue;
            pieces.add(new Piece(d, d.centralBankPrincipal(), d.getOustandingPrincipal(), dm.marketValue(d),
                    d.getSettleDue(), d.unsettledFace()));
        }
        return new Snap(cb.getPaperHeld(), dm.termPrincipal(), cb.thisMonth(CentralBank.Line.SOLD_PAPER),
                cb.thisMonth(CentralBank.Line.PAPER_GAINS), cb.thisMonth(CentralBank.Line.PAR_AT_ISSUE),
                cb.thisMonth(CentralBank.Line.BOUGHT_AT_ISSUE), cb.thisMonth(CentralBank.Line.PAR_AT_AUCTION),
                cb.thisMonth(CentralBank.Line.BOUGHT_AT_AUCTION), cb.thisMonth(CentralBank.Line.ISSUED), pieces);
    }

    /** A month played with the holdings read going in and at the probe. */
    record Month(Snap going, Snap probed) { }

    static Month probedMonth(Game g) {
        Snap going = snap(g);
        Snap[] at = new Snap[1];
        playProbed(g, () -> at[0] = snap(g));
        return new Month(going, at[0]);
    }

    /** The paper the treasury sold between the presses and the market has yet to pay for. */
    static List<Debt> waiting(Game g) {
        List<Debt> found = new ArrayList<>();
        for (Debt d : g.getDebtManager().getDebt()) {
            if (DebtManager.isTermPaper(d) && d.getSettleDue() > 0 && d.unsettledFace() > 0) found.add(d);
        }
        return found;
    }

    /** The treasury sells a five-year serial between the presses, as the fixture did: the face the market bought waits to settle. */
    static List<Debt> sellBetweenPresses(Game g) {
        double purse = g.getHouseholdBalance().spareForPaper();
        quietly(() -> g.handleMediumBondLogic(Math.max(SALE_FLOOR, purse * SALE_IN_PURSES), 5, 100 * PriceLevel.FACTOR));
        return waiting(g);
    }

    /** What a sale month was paid and gave up, read at the probe against the book before it. */
    record Sale(double paid, double expectedPaid, double faceSold, double gains, double term) { }

    /** A month played, with each piece's face given up by the central bank priced at its market value per face at the window. */
    static Sale saleMonth(Game g) {
        Month m = probedMonth(g);
        double expected = 0, face = 0;
        for (Piece p : m.probed().pieces()) {
            double gave = m.going().cbOf(p.d()) - p.cb();
            if (gave <= 0) continue;
            face += gave;
            expected += gave * p.market() / p.face();
        }
        return new Sale(m.probed().sold(), expected, face, m.probed().gains(), m.probed().term());
    }

    /** Whether the central bank's paper, at each piece's market value per face, is worth under its face by that share of it. */
    static boolean paperUnderFace(Game g, double share) {
        double worth = 0, face = 0;
        for (Piece p : snap(g).pieces()) {
            worth += p.cb() * p.market() / p.face();
            face += p.cb();
        }
        return face > 0 && worth < face * (1 - share);
    }

    /** The plain expression of Auto's step, in the model's own order of multiplication, so the two can be held to the bit. */
    static double autoStep(CentralBank cb, double term) {
        double held = cb.getPaperHeld() / term;
        return CentralBank.QE_SPEED * Math.max(Math.max(cb.getTargetShare(), cb.getPreviousTarget()), held) * term;
    }

    static boolean bits(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /* ============================ the fixture ============================ */

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        long started = System.nanoTime();
        buildTheTown();
        out.printf("   the town built in %.0fs%n", (System.nanoTime() - started) / 1e9);

        thePaceBothWays();
        theRunoffCapsHold();
        salesAtMarket();
        auctionPurchases();
        withTheLawOff();
        credibility();
        defaultsAndSaves();

        /* ============ 8. the money audit, to the cent ============ */
        out.println("\n--- 8. the money audit to the cent, over every month the sections played ---");
        out.printf("   %d months played; the worst residual %.6f (a cent is %.6f), the worst against what moved %.3e%n",
                monthsPlayed, worstResidual, MoneyAudit.CENT, worstRelative);
        check("every month any section played closed on the money audit", monthsPlayed > 0 && monthsOff == 0);
        check("...the worst residual of them is within a cent", worstResidual <= MoneyAudit.CENT);

        out.printf("%n   run time %.0fs%n", (System.nanoTime() - started) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails != 0) System.exit(fails);
    }

    /**
     * The one town every case loads a copy of: CentralBankCheck 19's, a five-year serial sold to the households, the bank and
     * (by the dial at .4) the central bank, played on until the month before its first slice falls due, and saved there. Saved
     * every month of the last stretch, so that what stays in the slot is the month before the one a slice is rolled in.
     */
    static void buildTheTown() {
        files = GameFiles.scratch("centralbankpacecheck");
        Game city = new Game(files);
        quietly(() -> {
            CentralBankCheck.standTheTown(city, 60);
            city.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        });
        soldIn = city.getMonth();
        double purse = city.getHouseholdBalance().spareForPaper();
        quietly(() -> city.handleMediumBondLogic(Math.max(SALE_FLOOR, purse * SALE_IN_PURSES), 5, 100 * PriceLevel.FACTOR));
        Debt serial = CentralBankCheck.fixtureSerial(city, soldIn);
        check("fixture: the treasury sold a five-year serial", serial != null);
        play(city);                                        // the settle: the households take their share
        /* The dial leaves the bank a part whatever the households take (CentralBankCheck 19): four fifths of the rest of the half they may hold. */
        double dial = (1 - HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE) * .8;
        city.getCentralBank().setTargetShare(dial);
        int months = 0;
        do {
            quietly(() -> city.saveGame(SLOT, "the month before the serial's first slice is rolled"));
            city.awaitSaves();
            play(city);
        } while (!(serial.principalDueNextMonth() > 0) && ++months < MOST_MONTHS);
        check("fixture: the serial's first slice falls due within a few years of its sale", serial.principalDueNextMonth() > 0);

        Game g = copy();
        Debt s = CentralBankCheck.fixtureSerial(g, soldIn);
        double term = g.getDebtManager().termPrincipal();
        out.printf("   m%d, saved: the serial $%,.0fk - the households $%,.0fk, the bank $%,.0fk, the central bank $%,.0fk; the central bank holds %.4f of the term paper%n",
                g.getMonth(), s.getOustandingPrincipal(), s.householdPrincipal(), s.bankPrincipal(), s.centralBankPrincipal(),
                g.getCentralBank().getPaperHeld() / term);
        check("fixture: the households, the bank and the central bank each hold part of the serial",
                s.householdPrincipal() > 0 && s.bankPrincipal() > 0 && s.centralBankPrincipal() > 0);
        near("fixture: the central bank has reached its dial", g.getCentralBank().getPaperHeld(), dial * term, 1e-6);
        check("fixture: nothing of the term paper falls due the month it is saved at", nothingFallsDue(g));
        check("fixture: the only term piece is the serial", g.getDebtManager().termPrincipal() == s.getOustandingPrincipal());
    }

    /* =====================================================================
       1. THE PACE BOTH WAYS
       ===================================================================== */

    static void thePaceBothWays() {
        out.println("\n--- 1. the pace both ways: a set pace, Auto's step, and the dial when it is nearer ---");

        /* ---- (a) a set pace, the dial well above the holding ---- */
        Game up = copy();
        CentralBank cbUp = up.getCentralBank();
        cbUp.holdingsRules().set(HoldingsRules.Dial.PACE, PACE);
        cbUp.setTargetShare(DIAL_UP);
        check("fixture: nothing falls due next month, and the dial is far above the holding",
                nothingFallsDue(up) && up.centralBankParFallingDue() == 0
                        && DIAL_UP - cbUp.getPaperHeld() / up.getDebtManager().termPrincipal() > 10 * PACE);
        Month mu = probedMonth(up);
        near("buying at a set pace: the holding rose by exactly the pace of the term paper",
                mu.probed().held() - mu.going().held(), PACE * mu.probed().term(), 1e-6);
        near("...the term paper the pace was struck on is the term paper before the month", mu.probed().term(), mu.going().term(), 1e-9);
        check("...and nothing was sold", mu.probed().sold() == 0);

        /* ---- (b) the dial well below the holding, sale mode ---- */
        Game down = copy();
        CentralBank cbDown = down.getCentralBank();
        cbDown.holdingsRules().set(HoldingsRules.Dial.PACE, PACE);
        cbDown.setTargetShare(DIAL_DOWN);
        check("fixture: sale mode, nothing falls due next month, and the dial is far below the holding",
                !cbDown.holdingsRules().runoffOnly() && nothingFallsDue(down) && down.centralBankParFallingDue() == 0
                        && cbDown.getPaperHeld() / down.getDebtManager().termPrincipal() - DIAL_DOWN > 10 * PACE);
        Month md = probedMonth(down);
        near("selling at a set pace: the holding fell by exactly the pace of the term paper",
                md.going().held() - md.probed().held(), PACE * md.probed().term(), 1e-6);

        /* ---- (c) Auto: its step to the bit from each of the three it takes the larger of, and a month that moves by it ---- */
        Game auto = copy();
        CentralBank cba = auto.getCentralBank();
        double termA = auto.getDebtManager().termPrincipal();
        check("fixture: the pace is on Auto", cba.holdingsRules().pace() == HoldingsRules.PACE_AUTO);
        cba.setTargetShare(DIAL_UP);                       // the dial is the largest
        check("Auto: with the dial the largest, the step is QE_SPEED of it, to the bit",
                bits(cba.stepFor(termA), autoStep(cba, termA)) && bits(cba.stepFor(termA), CentralBank.QE_SPEED * DIAL_UP * termA));
        cba.setTargetShare(DIAL_DOWN);                     // the setting before it (the .9) is the largest
        check("Auto: with the setting before it the largest, the step is QE_SPEED of that, to the bit",
                cba.getPreviousTarget() == DIAL_UP
                        && bits(cba.stepFor(termA), autoStep(cba, termA)) && bits(cba.stepFor(termA), CentralBank.QE_SPEED * DIAL_UP * termA));
        cba.setTargetShare(DIAL_DOWN / 2);                 // the share it holds is the largest
        double heldShare = cba.getPaperHeld() / termA;
        check("Auto: with the share it holds the largest, the step is QE_SPEED of that, to the bit",
                heldShare > cba.getPreviousTarget() && heldShare > cba.getTargetShare()
                        && bits(cba.stepFor(termA), autoStep(cba, termA)) && bits(cba.stepFor(termA), CentralBank.QE_SPEED * heldShare * termA));
        check("...and with no paper there is no step", cba.stepFor(0) == 0);

        Game far = copy();
        CentralBank cbf = far.getCentralBank();
        cbf.setTargetShare(DIAL_UP);
        double stepFar = cbf.stepFor(far.getDebtManager().termPrincipal());
        double gapFar = DIAL_UP * far.getDebtManager().termPrincipal() - cbf.getPaperHeld();
        check("fixture: the dial is further off than a step", stepFar > 0 && gapFar > stepFar);
        Month mf = probedMonth(far);
        near("a month at Auto moves the holding by exactly its step, the dial being further off than that",
                mf.probed().held() - mf.going().held(), stepFar, 1e-6);

        Game closeBy = copy();
        CentralBank cbn = closeBy.getCentralBank();
        double termN = closeBy.getDebtManager().termPrincipal();
        double dialNear = cbn.getPaperHeld() / termN + DIAL_NEAR;
        cbn.setTargetShare(dialNear);
        double stepNear = cbn.stepFor(termN);
        check("fixture: the dial is nearer than a step", DIAL_NEAR * termN < stepNear);
        Month mn = probedMonth(closeBy);
        near("...and when it is nearer than a step, it moves to the dial and no further",
                mn.probed().held(), dialNear * mn.probed().term(), 1e-6);
        near("...which is by what is left to the dial, less than the step", mn.probed().held() - mn.going().held(), DIAL_NEAR * termN, 1e-6);
    }

    /* =====================================================================
       2. THE RUNOFF'S CAPS HOLD
       ===================================================================== */

    /** What a roll month's runoff came to, with what it was struck on, read against the book as the press found it. */
    record Runoff(double par, double over, double term, double ranOff, double parAtIssue, double sold, double heldBefore,
                  double heldAfter, double heldAtProbe) { }

    /**
     * The fixture played to the month the serial's first slice is rolled in, under runoff only, with the pace the caller sets as
     * a share of the par that falls due and the dial at nothing (overOfPar of nothing or less) or leaving that share of the par
     * over it: the months before the slice sell nothing, and the slice month's runoff is read.
     */
    static Runoff runoffMonth(double overOfPar, double paceOfPar) {
        Game g = copy();
        CentralBank cb = g.getCentralBank();
        cb.holdingsRules().set(HoldingsRules.Dial.QT_MODE, HoldingsRules.QT_RUNOFF);
        cb.holdingsRules().set(HoldingsRules.Dial.PACE, PACE);
        cb.setTargetShare(0);
        Debt serial = CentralBankCheck.fixtureSerial(g, soldIn);
        double heldAtStart = cb.getPaperHeld();
        double soldInTheGap = 0;
        int months = 0;
        while (!(serial.principalDueNextMonth() > 0) && months++ < MOST_MONTHS) {
            play(g);
            soldInTheGap += cb.thisMonth(CentralBank.Line.SOLD_PAPER);
        }
        check("under runoff only, the months before the slice sold nothing and the holding did not move",
                months > 0 && soldInTheGap == 0 && Math.abs(cb.getPaperHeld() - heldAtStart) <= 1e-9 * heldAtStart);
        g.setCashForTest(0);                               // no cash for the rollover to net: its par is rolled or runs off
        double par = g.centralBankParFallingDue();
        double heldBefore = cb.getPaperHeld();
        double term = g.getDebtManager().termPrincipal();
        // The dial at nothing, or at the holding less that share of the par.
        cb.setTargetShare(overOfPar > 0 ? (heldBefore - overOfPar * par) / term : 0);
        cb.holdingsRules().set(HoldingsRules.Dial.PACE, paceOfPar * par / term);
        double over = g.centralBankOverItsDial();
        Month m = probedMonth(g);
        double ranOff = par - m.probed().parIssue();
        return new Runoff(par, over, term, ranOff, m.probed().parIssue(), cb.thisMonth(CentralBank.Line.SOLD_PAPER),
                heldBefore, cb.getPaperHeld(), m.probed().held());
    }

    static void theRunoffCapsHold() {
        out.println("\n--- 2. the runoff caps hold: nothing sold under runoff only; what runs off is the least of its par, its excess over the dial and the pace ---");

        /* ---- the pace binds: a half of the par a month, the holding far over the dial ---- */
        Runoff a = runoffMonth(0, .5);
        check("fixture: it holds a slice's par that falls due, and over its dial by far more than that par", a.par() > 0 && a.over() > 2 * a.par());
        near("with the pace under its par and the holding far over the dial, what ran off is exactly the pace of the term paper",
                a.ranOff(), .5 * a.par(), 1e-6);
        near("...and its par less that is what it rolled at issue", a.parAtIssue(), .5 * a.par(), 1e-6);
        check("...and nothing was sold that month (the SOLD_PAPER line is nothing)", a.sold() == 0);
        near("...so the book fell by what ran off", a.heldBefore() - a.heldAfter(), a.ranOff(), 1e-6);

        /* ---- the pace does not bind: two of its par, the holding far over the dial ---- */
        Runoff b = runoffMonth(0, 2);
        check("fixture: the pace is over its par and the holding is over its dial by more than the par", b.over() > b.par() && b.par() > 0);
        near("with the pace not binding, all its par over the dial ran off", b.ranOff(), b.par(), 1e-6);
        check("...none of it was rolled at issue", b.parAtIssue() <= 1e-6 * b.par());
        check("...and nothing was sold", b.sold() == 0);

        /* ---- the dial is nearer than the pace: the pace over everything, the holding a third of its par over the dial ---- */
        Runoff c = runoffMonth(1.0 / 3, 2);
        check("fixture: the holding is over its dial by under its par, and the pace is over that", c.over() > 0 && c.over() < c.par());
        near("with the pace not binding and the dial nearer, what ran off is what it held over its dial", c.ranOff(), c.over(), 1e-6);
        near("...which is a third of the par, and the rest of its par it rolled", c.parAtIssue(), c.par() - c.over(), 1e-6);
        check("...and nothing was sold", c.sold() == 0);

        /* ---- at the pace with sales, as Auto: no cap on the runoff (the rule before CBK2), and the pace is the sales' ---- */
        Game auto = copy();
        CentralBank cbAuto = auto.getCentralBank();
        double t = auto.getDebtManager().termPrincipal();
        check("Auto in sale mode has no runoff cap", cbAuto.runoffCap(t) == Double.POSITIVE_INFINITY && cbAuto.stepFor(t) > 0);
        cbAuto.holdingsRules().set(HoldingsRules.Dial.PACE, PACE);
        near("...a set pace caps the runoff at the pace of the term paper", cbAuto.runoffCap(t), PACE * t, 1e-12);
        cbAuto.holdingsRules().set(HoldingsRules.Dial.QT_MODE, HoldingsRules.QT_RUNOFF);
        near("...and so does runoff only", cbAuto.runoffCap(t), PACE * t, 1e-12);
        cbAuto.holdingsRules().set(HoldingsRules.Dial.PACE, HoldingsRules.PACE_AUTO);
        check("...and runoff only at Auto is capped at Auto's step, to the bit", bits(cbAuto.runoffCap(t), cbAuto.stepFor(t)));
    }

    /* =====================================================================
       3. SALES AT MARKET, GAINS AND LOSSES REALISED
       ===================================================================== */

    static void salesAtMarket() {
        out.println("\n--- 3. sales at market: the face given up at each piece's value is what the bank was paid, and the gain or loss is realised ---");

        /* ---- the paper as it stands ---- */
        Game g = copy();
        g.getCentralBank().holdingsRules().set(HoldingsRules.Dial.PACE, PACE);
        g.getCentralBank().setTargetShare(DIAL_DOWN);
        check("fixture: sale mode, nothing falls due next month, the dial far under the holding",
                nothingFallsDue(g) && !g.getCentralBank().holdingsRules().runoffOnly());
        double rate = g.getDebtManager().getPolicyRate();
        Sale s = saleMonth(g);
        out.printf("   the policy rate %.4f; it gave up $%,.1fk of face for $%,.1fk%n", rate, s.faceSold(), s.paid());
        check("it sold paper", s.faceSold() > 0);
        near("it gave up the pace of the term paper in face", s.faceSold(), PACE * s.term(), 1e-6);
        near("the face it gave up of each piece, at that piece's market value per face, is what the SOLD_PAPER line says it was paid (to the curve's drift to the probe)",
                s.expectedPaid(), s.paid(), CURVE_DRIFT);
        near("the PAPER_GAINS line is that price less the face it gave up: the gain or loss, realised", s.gains(), s.paid() - s.faceSold(),
                Math.max(1, s.faceSold()), 1e-9);

        /* ---- the rates up: the paper is under face, and it is sold at a loss ---- */
        Game loss = copy();
        loss.getCentralBank().holdingsRules().set(HoldingsRules.Dial.PACE, PACE);
        loss.getCentralBank().setTargetShare(DIAL_DOWN);
        for (int step = 0; step < MOST_RATE_STEPS && !paperUnderFace(loss, UNDER_FACE); step++) {
            loss.getDebtManager().setPolicyRate(loss.getDebtManager().getPolicyRate() + RATES_UP);
            loss.strikeTheCurve();
        }
        check("fixture: the rate is put up until the paper the central bank holds stands a hundredth under its face", paperUnderFace(loss, UNDER_FACE));
        Sale l = saleMonth(loss);
        out.printf("   the policy rate %.4f; it gave up $%,.1fk of face for $%,.1fk%n", loss.getDebtManager().getPolicyRate(), l.faceSold(), l.paid());
        check("fixture: with the rate up the paper sold was worth less than its face", l.faceSold() > 0 && l.paid() < l.faceSold());
        near("the price is the face given up at each piece's market value per face (to the curve's drift)", l.expectedPaid(), l.paid(), CURVE_DRIFT);
        check("the loss is realised: the PAPER_GAINS line is negative", l.gains() < 0);
        near("...and it is the same price less the face", l.gains(), l.paid() - l.faceSold(), Math.max(1, l.faceSold()), 1e-9);

        /* ---- the rates down: the paper is over face, and it is sold at a gain ---- */
        Game gain = copy();
        gain.getCentralBank().holdingsRules().set(HoldingsRules.Dial.PACE, PACE);
        gain.getCentralBank().setTargetShare(DIAL_DOWN);
        gain.getDebtManager().setPolicyRate(gain.getDebtManager().getPolicyRate() / 2);
        gain.strikeTheCurve();
        Sale w = saleMonth(gain);
        out.printf("   the policy rate %.4f; it gave up $%,.1fk of face for $%,.1fk%n", gain.getDebtManager().getPolicyRate(), w.faceSold(), w.paid());
        check("fixture: with the rate halved the paper sold was worth more than its face", w.faceSold() > 0 && w.paid() > w.faceSold());
        near("the price is the face given up at each piece's market value per face (to the curve's drift)", w.expectedPaid(), w.paid(), CURVE_DRIFT);
        check("the gain is realised: the PAPER_GAINS line is positive", w.gains() > 0);
        near("...and it is the price less the face", w.gains(), w.paid() - w.faceSold(), Math.max(1, w.faceSold()), 1e-9);
    }

    /* =====================================================================
       4. AUCTION PURCHASES AT THE AUCTION'S PRICE AND INSIDE THE CAP
       ===================================================================== */

    /** The central bank under the law, with the share it bids; the treasury has sold new paper between the presses. */
    static Game underTheLaw(double share) {
        Game g = copy();
        HoldingsRules rules = g.getCentralBank().holdingsRules();
        rules.set(HoldingsRules.Dial.DIRECT_PURCHASES, HoldingsRules.LAW_ON);
        rules.set(HoldingsRules.Dial.AUCTION_SHARE, share);
        return g;
    }

    static void auctionPurchases() {
        out.println("\n--- 4. auction purchases: at the auction's price, the share of what the market bought, inside the dial's room, and made money ---");

        /* ---- (a) the dial at all of it: no room to limit it ---- */
        Game a = underTheLaw(SHARE);
        a.getCentralBank().setTargetShare(1.0);
        List<Debt> sold = sellBetweenPresses(a);
        check("fixture: the law is on, the share is a quarter, and the treasury sold one new piece the market has yet to pay for",
                a.getCentralBank().holdingsRules().directPurchasesAllowed() && a.getCentralBank().holdingsRules().auctionShare() == SHARE
                        && nothingFallsDue(a) && sold.size() == 1);
        Debt piece = sold.get(0);
        double face = piece.unsettledFace(), due = piece.getSettleDue(), heldInIt = piece.centralBankPrincipal();
        Month m = probedMonth(a);
        near("with no room to limit it, it bought the share of the face the market bought", m.probed().parAuction(), SHARE * face, 1e-9);
        near("...at the auction's price on each unit of face: what the market owed the treasury for the piece, over its face",
                m.probed().boughtAuction() / m.probed().parAuction(), due / face, 1e-9);
        near("...so its payment is the share's face at that price", m.probed().boughtAuction(), SHARE * face * due / face, 1e-9);
        near("...and the new piece carries its share, over what it held of it", piece.centralBankPrincipal() - heldInIt, SHARE * face, 1e-9);
        check("...none of it a rollover of its own: nothing it holds fell due", m.probed().parIssue() == 0 && m.probed().boughtIssue() == 0);

        /* ---- (c) the money made: on the statement's MADE lines, and closing ---- */
        CentralBank cb = a.getCentralBank();
        SectorStatements.Table money = CentralBankStatements.money(cb);
        double madeLines = 0;
        for (Object[] w : CentralBankStatements.MADE) madeLines += cb.thisMonth((CentralBank.Line) w[1]);
        check("the money it made this month includes what it paid at auction", cb.getIssued() >= cb.getBoughtAtAuction() && cb.getBoughtAtAuction() > 0);
        near("the money statement's line for it is what it paid at auction",
                money.now(CentralBankStatements.moneyId(CentralBank.Line.BOUGHT_AT_AUCTION)), cb.getBoughtAtAuction(), 1e-12);
        near("the MADE lines, the auction's among them, sum to the money it issued", madeLines, cb.getIssued(), 1e-9);
        near("...and the statement's money made has nothing NOT ACCOUNTED FOR",
                money.now(CentralBankStatements.M_MADE_REST), 0, Math.max(1, cb.getIssued()), 1e-9);
        near("...the statement's total is getIssued()", money.now(CentralBankStatements.M_MADE), cb.getIssued(), 1e-12);
        near("...and the line's twin, par at auction, is what it took of the piece", cb.getParAtAuction(), SHARE * face, 1e-9);

        /* ---- (b) the dial a little over what it would hold: the room is the cap ---- */
        Game cap = underTheLaw(SHARE);
        List<Debt> soldCap = sellBetweenPresses(cap);
        check("fixture: the treasury sold one new piece", soldCap.size() == 1);
        Debt capPiece = soldCap.get(0);
        double bid = SHARE * capPiece.unsettledFace();
        Snap goingCap = snap(cap);
        double room = ROOM_OF_BID * bid;
        // Its holding with the room taken, as a share of the term paper with the room added: the dial at which the room is that much.
        double dial = (goingCap.held() + room) / (goingCap.term() + room);
        cap.getCentralBank().setTargetShare(dial);
        check("fixture: the dial is a little over the share it holds of the paper including the new piece, and the room is under the bid",
                dial > goingCap.held() / goingCap.term() && dial < 1 && room < bid);
        Snap[] atCap = new Snap[1];
        playProbed(cap, () -> atCap[0] = snap(cap));
        near("with the dial near, what it took at auction is the room under the dial, which is half of the share's bid",
                atCap[0].parAuction(), room, 1e-6);
        check("...less than the share's bid", atCap[0].parAuction() < bid);
        near("...and at the probe its holding is the dial's share of the term paper, the new piece's face included",
                atCap[0].held(), dial * atCap[0].term(), 1e-6);
        check("...never over it", atCap[0].held() <= dial * atCap[0].term() * (1 + 1e-9));

        /* ---- no room: at its dial or over it, none ---- */
        Game at = underTheLaw(SHARE);
        check("fixture: the treasury sold one new piece", sellBetweenPresses(at).size() == 1);
        Snap goingAt = snap(at);
        at.getCentralBank().setTargetShare(goingAt.held() / goingAt.term());
        Snap[] atDial = new Snap[1];
        playProbed(at, () -> atDial[0] = snap(at));
        check("with its holding at its dial, there is no room and it took none at auction",
                atDial[0].parAuction() == 0 && atDial[0].boughtAuction() == 0);

        Game over = underTheLaw(SHARE);
        check("fixture: the treasury sold one new piece", sellBetweenPresses(over).size() == 1);
        Snap goingOver = snap(over);
        over.getCentralBank().setTargetShare(goingOver.held() / goingOver.term() - DIAL_NEAR);
        Snap[] overDial = new Snap[1];
        playProbed(over, () -> overDial[0] = snap(over));
        check("...and with its holding over its dial, it took none at auction",
                overDial[0].parAuction() == 0 && overDial[0].boughtAuction() == 0);

        /* ---- (d) two new pieces: pro rata to the face the market bought, each at its own price ---- */
        Game two = underTheLaw(SHARE);
        two.getCentralBank().setTargetShare(1.0);
        sellBetweenPresses(two);
        quietly(() -> two.handleLongBondLogic(5_000 * PriceLevel.FACTOR, 20, 100 * PriceLevel.FACTOR));
        List<Debt> both = waiting(two);
        check("fixture: the treasury sold two new pieces between the presses", both.size() == 2);
        double[] faces = new double[both.size()], dues = new double[both.size()], heldIn = new double[both.size()];
        double wanted = 0, pay = 0;
        for (int i = 0; i < both.size(); i++) {
            faces[i] = both.get(i).unsettledFace();
            dues[i] = both.get(i).getSettleDue();
            heldIn[i] = both.get(i).centralBankPrincipal();
            wanted += SHARE * faces[i];
            pay += SHARE * faces[i] * dues[i] / faces[i];
        }
        Month mt = probedMonth(two);
        near("with two pieces, it took the share of all the face the market bought", mt.probed().parAuction(), wanted, 1e-9);
        for (int i = 0; i < both.size(); i++) {
            near("...of each piece, the share of that piece's own face", both.get(i).centralBankPrincipal() - heldIn[i], SHARE * faces[i], 1e-9);
        }
        near("...and paid each piece's own price on each unit of its face", mt.probed().boughtAuction(), pay, 1e-9);
    }

    /* =====================================================================
       5. WITH THE LAW OFF, NO NEW PAPER AT AUCTION BEYOND THE ROLLOVER
       ===================================================================== */

    static void withTheLawOff() {
        out.println("\n--- 5. with the law off, no new paper at auction beyond the rollover, whatever the share; with the law on and no share, none ---");

        /* ---- new paper sold, the share half, the law off: nothing it holds falls due, so nothing at all ---- */
        Game off = copy();
        HoldingsRules rules = off.getCentralBank().holdingsRules();
        rules.set(HoldingsRules.Dial.AUCTION_SHARE, .5);
        off.getCentralBank().setTargetShare(1.0);
        check("fixture: the share is half and the law is off, so the share in force is nothing",
                rules.get(HoldingsRules.Dial.AUCTION_SHARE) == .5 && !rules.directPurchasesAllowed() && rules.auctionShare() == 0);
        List<Debt> sold = sellBetweenPresses(off);
        check("fixture: the treasury sold one new piece and nothing the bank holds falls due", sold.size() == 1 && nothingFallsDue(off));
        Debt piece = sold.get(0);
        double heldInIt = piece.centralBankPrincipal();
        Month m = probedMonth(off);
        check("with the law off, it bought nothing at auction", m.probed().parAuction() == 0 && m.probed().boughtAuction() == 0);
        check("...its par at issue is its own rollover's, and nothing it holds fell due", m.probed().parIssue() == 0 && m.probed().boughtIssue() == 0);
        near("...and the new piece's holding of the central bank is what it was before the window", m.probed().cbOf(piece), heldInIt, 0);

        /* ---- the law on, a share of nothing ---- */
        Game none = underTheLaw(0);
        none.getCentralBank().setTargetShare(1.0);
        List<Debt> soldNone = sellBetweenPresses(none);
        check("fixture: the law is on, the share is nothing, and the treasury sold one new piece",
                none.getCentralBank().holdingsRules().directPurchasesAllowed() && soldNone.size() == 1);
        Month mn = probedMonth(none);
        check("with the law on and the share nothing, it took nothing at auction", mn.probed().parAuction() == 0 && mn.probed().boughtAuction() == 0);
        near("...and the new piece's holding of the central bank is what it was", mn.probed().cbOf(soldNone.get(0)), 0, 0);

        /* ---- the rollover is its own, and the share is not added to it: the slice's month with the law off ---- */
        Game roll = copy();
        HoldingsRules rr = roll.getCentralBank().holdingsRules();
        rr.set(HoldingsRules.Dial.AUCTION_SHARE, .5);
        Debt serial = CentralBankCheck.fixtureSerial(roll, soldIn);
        int months = 0;
        while (!(serial.principalDueNextMonth() > 0) && months++ < MOST_MONTHS) play(roll);
        roll.setCashForTest(0);
        double par = roll.centralBankParFallingDue();
        CentralBank cb = roll.getCentralBank();
        double term = roll.getDebtManager().termPrincipal();
        cb.setTargetShare(cb.getPaperHeld() / term);       // at its dial: it rolls all its par
        check("fixture: the law is off, the share half, a slice falls due and the central bank holds part of it, at its dial",
                !rr.directPurchasesAllowed() && par > 0 && par < serial.principalDueNextMonth() && roll.centralBankOverItsDial() == 0);
        Month mr = probedMonth(roll);
        near("in the month its own paper falls due, it rolled its par at issue", mr.probed().parIssue(), par, 1e-9);
        check("...and took nothing at auction beyond it, though the share is half", mr.probed().parAuction() == 0 && mr.probed().boughtAuction() == 0);
        double onNew = 0;
        for (Piece p : mr.probed().pieces()) if (mr.going().of(p.d()) == null) onNew += p.cb();
        near("...the new paper it holds is its par and no more", onNew, par, 1e-9);
    }

    /* =====================================================================
       6. CREDIBILITY
       ===================================================================== */

    /** The share of a year's GDP each case lent its treasury (docs/queue/CBK2-notes.md, "Reality checked"): Indonesia 2020, anchored; Argentina 2020 and Ghana 2022, unanchored within a year. */
    static final double INDONESIA = .03, ARGENTINA = .074, GHANA = .072;

    /** Months the bounded case lends all of a year's GDP: fifty years. */
    static final int LONG_MONTHS = 600;

    /** Months the twins' treasury sells new paper between the presses: ten, the months before the serial's next slice. */
    static final int TWIN_MONTHS = 10;

    /** The most months the lending twin is played after it stops, waiting for its stock to fall under the anchored share. */
    static final int MOST_STOP_MONTHS = 120;

    /** The face the twins' treasury sells between the presses each month, a share of the year's GDP: a year of it at a share of all of it is past the unanchored share. */
    static final double SALE_OF_GDP = .01;

    /** A bare Expectations, its credibility at k and its inflation at what Argentina's stood at when it began (restore()'s five slots). */
    static Expectations believing(double k) {
        Expectations x = new Expectations();
        x.reset();
        x.restore(new double[] { k, .4, .4, 1, 1 });
        return x;
    }

    /** One month of financed(): its credibility after, by the model's own arithmetic in its own order. */
    static double afterLending(double k, double x) {
        return Math.max(Expectations.KMIN, Math.min(Expectations.KMAX, k - (k - Expectations.KMIN) * x / Expectations.FINANCING_LOSS_MONTHS));
    }

    /** ...and the premium it strikes at that credibility. */
    static double premiumAt(double k, double x) {
        return Expectations.FINANCING_PREMIUM * x * (Expectations.KMAX - k) / (Expectations.KMAX - Expectations.KMIN);
    }

    static void credibility() {
        out.println("\n--- 6. credibility: lending the treasury at auction past the anchored share costs trust, adds to expected inflation and weakens the currency ---");
        double anchored = Expectations.FINANCING_ANCHORED, unanchored = Expectations.FINANCING_UNANCHORED;

        /* ---- the strength: nothing to the anchored share, all at the unanchored, a straight line between ---- */
        check("lending nothing, or less than nothing, or a figure that is no number, costs nothing",
                Expectations.financingStrength(0) == 0 && Expectations.financingStrength(-.05) == 0 && Expectations.financingStrength(Double.NaN) == 0);
        check("...nor lending the anchored share a year, Indonesia's 3% of GDP in 2020 among them",
                Expectations.financingStrength(anchored) == 0 && Expectations.financingStrength(INDONESIA) == 0);
        check("...a little over it costs a little", Expectations.financingStrength(anchored * 1.001) > 0 && Expectations.financingStrength(anchored * 1.001) < .01);
        near("...halfway to the unanchored share costs half", Expectations.financingStrength((anchored + unanchored) / 2), .5, 1e-12);
        check("...the unanchored share costs all of it, and Argentina's 7.4% and Ghana's 7.2% of GDP no less, nor all of a year's GDP or more",
                Expectations.financingStrength(unanchored) == 1 && Expectations.financingStrength(ARGENTINA) == 1 && Expectations.financingStrength(GHANA) == 1
                        && Expectations.financingStrength(1) == 1 && Expectations.financingStrength(Double.POSITIVE_INFINITY) == 1);

        /* ---- a month of it on a bare Expectations ---- */
        Expectations none = believing(Expectations.KSEED);
        none.financed(0);
        check("a bank that lent nothing loses no trust and adds no premium",
                bits(none.getCredibility(), Expectations.KSEED) && none.getFinancingPremium() == 0);
        none.financed(anchored);
        check("...nor one that lent the anchored share", bits(none.getCredibility(), Expectations.KSEED) && none.getFinancingPremium() == 0);
        Expectations half = believing(Expectations.KSEED);
        double xHalf = Expectations.financingStrength((anchored + unanchored) / 2);
        half.financed((anchored + unanchored) / 2);
        check("halfway, credibility falls half a FINANCING_LOSS_MONTHS-th of the way to KMIN, to the bit",
                bits(half.getCredibility(), afterLending(Expectations.KSEED, xHalf)));
        check("...and the premium is half of FINANCING_PREMIUM, in proportion to the trust lost, to the bit",
                bits(half.getFinancingPremium(), premiumAt(half.getCredibility(), xHalf)));
        Expectations full = believing(Expectations.KSEED);
        full.financed(unanchored);
        double kFull = afterLending(Expectations.KSEED, 1);
        check("at the full cost, credibility falls a FINANCING_LOSS_MONTHS-th of the way to KMIN, to the bit", bits(full.getCredibility(), kFull));
        check("...and the premium is FINANCING_PREMIUM in proportion to the trust lost, to the bit", bits(full.getFinancingPremium(), premiumAt(kFull, 1)));
        full.financed(0);
        check("the premium is the month's: a month that lends nothing adds none, and the trust lost stays lost",
                full.getFinancingPremium() == 0 && bits(full.getCredibility(), kFull));

        /* ---- bounded both ways over fifty years of all of a year's GDP ---- */
        Expectations ever = believing(Expectations.KMAX);
        double lowest = 1, highest = 0;
        for (int k = 0; k < LONG_MONTHS; k++) {
            ever.financed(1);
            lowest = Math.min(lowest, ever.getCredibility());
            highest = Math.max(highest, ever.getFinancingPremium());
        }
        check("fifty years of lending all of a year's GDP never takes credibility under KMIN", lowest >= Expectations.KMIN);
        check("...nor the premium over FINANCING_PREMIUM", highest <= Expectations.FINANCING_PREMIUM);
        near("...and ends at KMIN with all of FINANCING_PREMIUM", ever.getFinancingPremium(), Expectations.FINANCING_PREMIUM, 1e-9);

        /* ---- the cases ---- */
        Expectations argentina = believing(Expectations.KMIN);
        argentina.financed(ARGENTINA);
        near("a bank believed as little as Argentina's in 2020 adds Argentina's nine points the first month of lending its 7.4%",
                argentina.getFinancingPremium(), .09, 1e-12);
        Expectations ghana = believing(Expectations.KSEED);
        double kGhana = Expectations.KSEED;
        for (int k = 0; k < 3; k++) { ghana.financed(GHANA); kGhana = afterLending(kGhana, 1); }
        check("from a new city's trust, three months of Ghana's 7.2% add over half of FINANCING_PREMIUM: expectations moved within one to three months",
                ghana.getFinancingPremium() > Expectations.FINANCING_PREMIUM / 2 && bits(ghana.getCredibility(), kGhana));
        for (int k = 3; k < 12; k++) ghana.financed(GHANA);
        check("...and a year of it leaves less than a quarter of the trust it had above KMIN: unanchored within a year",
                ghana.getCredibility() - Expectations.KMIN < .25 * (Expectations.KSEED - Expectations.KMIN));

        /* ---- the month's expectation carries the premium: takeMonth on a based basket ---- */
        PriceIndex index = new PriceIndex();
        int month = 0;
        while (!index.isBased()) index.takeMonth(.3, .4, 100, 100, ++month);
        double target = DebtManager.DEFAULT_INFLATION_TARGET, rate = .03;
        Expectations plainX = believing(Expectations.KSEED), lentX = believing(Expectations.KSEED);
        plainX.financed(0);
        lentX.financed(unanchored);
        plainX.takeMonth(index, target, rate, rate, rate);
        lentX.takeMonth(index, target, rate, rate, rate);
        double kp = plainX.getCredibility(), kl = lentX.getCredibility();
        check("the month's expected inflation of a bank that lent nothing is the anchored figure, to the bit",
                bits(plainX.getExpectedInflation(), Math.max(Expectations.EXPECTED_FLOOR, kp * target + (1 - kp) * plainX.getSmoothedInflation())));
        check("...and of one that lent the unanchored share, the anchored figure and the premium, to the bit",
                bits(lentX.getExpectedInflation(), Math.max(Expectations.EXPECTED_FLOOR,
                        kl * target + (1 - kl) * lentX.getSmoothedInflation() + lentX.getFinancingPremium())));
        check("...which is the more, by more than the premium alone where inflation runs over the target",
                lentX.getExpectedInflation() > plainX.getExpectedInflation() && lentX.getFinancingPremium() > 0);
        check("...and the month's fall in credibility is in its step", lentX.getCredibilityStep() < plainX.getCredibilityStep());

        /* ---- twin towns: one whose bank takes the market's face at auction ---- */
        Game plain = copy(), lent = copy();
        for (Game g : new Game[] { plain, lent }) {
            HoldingsRules r = g.getCentralBank().holdingsRules();
            r.set(HoldingsRules.Dial.DIRECT_PURCHASES, HoldingsRules.LAW_ON);
            r.set(HoldingsRules.Dial.PACE, HoldingsRules.Dial.PACE.step);   // the smallest pace: the open market's step is small and the same in both
            g.getCentralBank().setTargetShare(1.0);                         // no room limit: the auction share is all that differs
            quietly(() -> g.getDebtManager().setAutopilot(false));         // the rate held by hand: the gap is the expectations'
        }
        check("fixture: the twins' currency floats (the fixture's pin is not saved) and their rate is held by hand",
                !plain.getForeignAccounts().isPinned() && !lent.getForeignAccounts().isPinned()
                        && !plain.getDebtManager().isAutopilot() && !lent.getDebtManager().isAutopilot());
        /* The slice the fixture is saved before is refinanced at an auction the share would bid at too (every term issue is,
           as the Bank of Canada bids at every bond auction): the twins roll it alike first, so the share meets only the
           treasury's own sales, a modest share of the year's GDP and not the serial's. */
        for (int k = 0; nothingFallsDue(plain) && k < MOST_MONTHS; k++) { play(plain); play(lent); }
        play(plain);                                   // the slice's month: rolled alike
        play(lent);
        lent.getCentralBank().holdingsRules().set(HoldingsRules.Dial.AUCTION_SHARE, 1.0);
        boolean everySold = true, stockTurns = true, nothingDue = true;
        double plainTook = 0, soldFace = 0, tookFace = 0;
        for (int k = 0; k < TWIN_MONTHS; k++) {
            nothingDue &= nothingFallsDue(plain) && nothingFallsDue(lent);
            double face = SALE_OF_GDP * plain.getEconomyManager().getNationalAccounts().getAnnualGdp();
            for (Game g : new Game[] { plain, lent }) {
                quietly(() -> g.handleMediumBondLogic(face, 5, 100 * PriceLevel.FACTOR));
            }
            everySold &= !waiting(plain).isEmpty() && !waiting(lent).isEmpty();
            for (Debt d : waiting(lent)) soldFace += d.unsettledFace();
            double stock = lent.getCentralBank().getFinancedLately();
            play(plain);
            play(lent);
            CentralBank cb = lent.getCentralBank();
            stockTurns &= bits(cb.getFinancedLately(), stock * (1 - 1.0 / CentralBank.FINANCING_MONTHS) + cb.getBoughtAtAuction());
            plainTook += plain.getCentralBank().getBoughtAtAuction();
            tookFace += cb.getParAtAuction();
        }
        CentralBank pcb = plain.getCentralBank(), lcb = lent.getCentralBank();
        Expectations pe = plain.getExpectations(), le = lent.getExpectations();
        double gdp = lent.getEconomyManager().getNationalAccounts().getAnnualGdp();
        out.printf("   %d months: the year's GDP $%,.1fk; the market bought $%,.1fk of new paper and the bank $%,.1fk at auction; lent lately %.1f%% of a year's GDP%n",
                TWIN_MONTHS, gdp, soldFace, tookFace, 100 * lcb.getFinancedLately() / gdp);
        out.printf("   credibility %.3f against %.3f; expected %.2f%% against %.2f%% (the premium %.2f points); D$ per US$ %.4f against %.4f%n",
                le.getCredibility(), pe.getCredibility(), 100 * le.getExpectedInflation(), 100 * pe.getExpectedInflation(),
                100 * le.getFinancingPremium(), lent.getForeignAccounts().getRate(), plain.getForeignAccounts().getRate());
        check("fixture: nothing of the term paper fell due in the twins' year, and the treasury sold new paper between the presses every month in both",
                nothingDue && everySold);
        check("the twin whose share is nothing lent nothing at auction, owes nothing and adds no premium",
                plainTook == 0 && pcb.getParAtAuctionLifetime() == 0 && pcb.getFinancedLately() == 0 && pe.getFinancingPremium() == 0);
        near("the twin whose share is all of it took at auction all the face the market bought", tookFace, soldFace, 1e-9);
        check("...its lending stock turned every month: a twelfth forgotten and the month's auction purchases added, to the bit", stockTurns);
        check("...it has lent past the unanchored share of a year's GDP", lcb.getFinancedLately() / gdp > unanchored);
        check("...so it is believed less", le.getCredibility() < pe.getCredibility());
        check("...expects more inflation, the premium in it", le.getExpectedInflation() > pe.getExpectedInflation() && le.getFinancingPremium() > 0);
        check("...with the same policy rate, so its real rate gap is the narrower",
                bits(lent.getDebtManager().getPolicyRate(), plain.getDebtManager().getPolicyRate()) && lent.realRateDifferential() < plain.realRateDifferential());
        check("...and its currency is the weaker: more D$ to the US$", lent.getForeignAccounts().getRate() > plain.getForeignAccounts().getRate());

        /* ---- it stops: the stock is forgotten a twelfth a month; under the anchored share no premium; trust back only at GAIN_MONTHS ---- */
        lcb.holdingsRules().set(HoldingsRules.Dial.AUCTION_SHARE, 0);
        double k0 = le.getCredibility();
        boolean forgotten = true;
        int months = 0;
        while (!(lcb.getFinancedLately() < anchored * lent.getEconomyManager().getNationalAccounts().getAnnualGdp()) && months < MOST_STOP_MONTHS) {
            double before = lcb.getFinancedLately();
            play(lent);
            months++;
            forgotten &= lcb.getBoughtAtAuction() == 0 && bits(lcb.getFinancedLately(), before * (1 - 1.0 / CentralBank.FINANCING_MONTHS));
        }
        play(lent);
        months++;
        out.printf("   stopped: %d months to under the anchored share; credibility %.3f from %.3f%n", months, le.getCredibility(), k0);
        check("stopped, it buys nothing at auction and its stock falls a twelfth a month, to the bit", forgotten && months < MOST_STOP_MONTHS);
        check("...and the month after it is under the anchored share of a year's GDP, it adds no premium", le.getFinancingPremium() == 0);
        double gained = (Expectations.KMAX - k0) * (1 - Math.pow(1 - 1.0 / Expectations.GAIN_MONTHS, months));
        check("...and its credibility climbs back no faster than GAIN_MONTHS allows", le.getCredibility() <= k0 + gained + 1e-12);
    }


    /* =====================================================================
       7. DEFAULTS AND SAVES
       ===================================================================== */

    /** A month of a city as the figures the rules touch read it: the holding, the term paper, the bank's issue and lines, expectations, the currency. */
    static double[] figures(Game g) {
        CentralBank cb = g.getCentralBank();
        Expectations x = g.getExpectations();
        return new double[] { cb.getPaperHeld(), g.getDebtManager().termPrincipal(), cb.getIssuedLifetime(),
                cb.thisMonth(CentralBank.Line.ISSUED), cb.thisMonth(CentralBank.Line.SOLD_PAPER), cb.thisMonth(CentralBank.Line.PAR_AT_ISSUE),
                x.getCredibility(), x.getExpectedInflation(), g.getForeignAccounts().getRate(), g.getEconomyManager().getNationalAccounts().getAnnualGdp(),
                g.getLastMoneyAudit() == null ? 0 : g.getLastMoneyAudit().residual };
    }

    static boolean sameBits(double[] a, double[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (!bits(a[i], b[i])) return false;
        return true;
    }

    /** The slot's files copied to another scratch folder, so a case can rewrite them without touching the fixture. */
    static GameFiles copiedTo(String label) throws java.io.IOException {
        GameFiles to = GameFiles.scratch(label);
        java.nio.file.Files.createDirectories(to.savesDirectory());
        for (java.nio.file.Path p : new java.nio.file.Path[] { files.saveFile(SLOT), files.historyFile(SLOT), files.mapFile(SLOT) }) {
            if (java.nio.file.Files.exists(p)) {
                java.nio.file.Files.copy(p, to.savesDirectory().resolve(p.getFileName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return to;
    }

    static Game loaded(GameFiles from) {
        Game g = new Game(from);
        quietly(() -> g.loadGameSave(SLOT));
        g.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        return g;
    }

    static void defaultsAndSaves() throws Exception {
        out.println("\n--- 7. defaults and saves: the rules before CBK2 by default, every rule and figure back from a save, and a save from before CBK2 as it was ---");

        /* ---- the defaults ---- */
        HoldingsRules fresh = new HoldingsRules();
        check("a new city's pace is Auto, its book shrinks by runoff and sales, its share is nothing and the law is off",
                fresh.get(HoldingsRules.Dial.PACE) == HoldingsRules.PACE_AUTO && fresh.get(HoldingsRules.Dial.QT_MODE) == HoldingsRules.QT_SELL
                        && fresh.get(HoldingsRules.Dial.AUCTION_SHARE) == 0 && fresh.get(HoldingsRules.Dial.DIRECT_PURCHASES) == HoldingsRules.LAW_OFF);
        check("...so it reads no pace, sells, bids nothing at auction and is allowed no direct purchase",
                fresh.pace() == 0 && !fresh.runoffOnly() && fresh.auctionShare() == 0 && !fresh.directPurchasesAllowed());
        fresh.set(HoldingsRules.Dial.AUCTION_SHARE, .4);
        check("a share set with the law off bids nothing", fresh.auctionShare() == 0);
        fresh.set(HoldingsRules.Dial.DIRECT_PURCHASES, HoldingsRules.LAW_ON);
        check("...and under the law it is the share", fresh.auctionShare() == .4);
        fresh.set(HoldingsRules.Dial.QT_MODE, .7);
        fresh.set(HoldingsRules.Dial.DIRECT_PURCHASES, .3);
        check("the two switches are one or the other, never between",
                fresh.get(HoldingsRules.Dial.QT_MODE) == HoldingsRules.QT_RUNOFF && fresh.get(HoldingsRules.Dial.DIRECT_PURCHASES) == HoldingsRules.LAW_OFF);
        Game at = copy();
        HoldingsRules atRules = at.getCentralBank().holdingsRules();
        double term = at.getDebtManager().termPrincipal();
        check("the fixture, saved at the defaults, loads at them",
                atRules.pace() == 0 && !atRules.runoffOnly() && atRules.auctionShare() == 0 && !atRules.directPurchasesAllowed());
        check("...where the step is Auto's to the bit and the runoff has no cap, the rules before CBK2",
                bits(at.getCentralBank().stepFor(term), autoStep(at.getCentralBank(), term)) && at.getCentralBank().runoffCap(term) == Double.POSITIVE_INFINITY);

        /* ---- every rule and figure back from a save: a city of its own folder, so the fixture's slot is never written ---- */
        Game set = loaded(copiedTo("centralbankpacecheck-roundtrip"));
        HoldingsRules sr = set.getCentralBank().holdingsRules();
        sr.set(HoldingsRules.Dial.DIRECT_PURCHASES, HoldingsRules.LAW_ON);
        sr.set(HoldingsRules.Dial.AUCTION_SHARE, SHARE);
        sr.set(HoldingsRules.Dial.PACE, 2 * PACE);
        sr.set(HoldingsRules.Dial.QT_MODE, HoldingsRules.QT_RUNOFF);
        set.getCentralBank().setTargetShare(1.0);
        sellBetweenPresses(set);
        play(set);
        CentralBank scb = set.getCentralBank();
        check("fixture: every rule is off its default, and under the law it took a share of the market's face at auction this month",
                sr.pace() > 0 && sr.runoffOnly() && sr.auctionShare() == SHARE && scb.getParAtAuction() > 0 && scb.getFinancedLately() > 0);
        quietly(() -> set.saveGame(SLOT, "every rule set"));
        set.awaitSaves();
        Game back = loaded(set.getGameFiles());
        CentralBank bcb = back.getCentralBank();
        HoldingsRules br = bcb.holdingsRules();
        boolean rulesBack = true;
        for (HoldingsRules.Dial d : HoldingsRules.Dial.values()) rulesBack &= bits(br.get(d), sr.get(d));
        check("every rule comes back from a save to the bit", rulesBack);
        check("...the auction's purchases and par since founding, and the lending stock",
                bits(bcb.getBoughtAtAuctionLifetime(), scb.getBoughtAtAuctionLifetime()) && bits(bcb.getParAtAuctionLifetime(), scb.getParAtAuctionLifetime())
                        && bits(bcb.getFinancedLately(), scb.getFinancedLately()));
        check("...the month's auction lines",
                bits(bcb.thisMonth(CentralBank.Line.BOUGHT_AT_AUCTION), scb.thisMonth(CentralBank.Line.BOUGHT_AT_AUCTION))
                        && bits(bcb.thisMonth(CentralBank.Line.PAR_AT_AUCTION), scb.thisMonth(CentralBank.Line.PAR_AT_AUCTION)));
        check("...and the premium and credibility",
                bits(back.getExpectations().getFinancingPremium(), set.getExpectations().getFinancingPremium())
                        && bits(back.getExpectations().getCredibility(), set.getExpectations().getCredibility()));
        play(set);
        play(back);
        check("...and the saved city and the one it was saved from play their next month to the bit", sameBits(figures(back), figures(set)));
        Expectations unit = believing(Expectations.KSEED);
        unit.financed(Expectations.FINANCING_UNANCHORED);
        Expectations unitBack = new Expectations();
        unitBack.restore(unit.toSaveArray());
        check("an Expectations' premium comes back from its array to the bit, the array SAVE_SLOTS long",
                unit.getFinancingPremium() > 0 && bits(unitBack.getFinancingPremium(), unit.getFinancingPremium())
                        && unit.toSaveArray().length == Expectations.SAVE_SLOTS);
        Expectations older = new Expectations();
        older.restore(java.util.Arrays.copyOf(unit.toSaveArray(), Expectations.SAVE_SLOTS - 1));
        check("...and an array from before CBK2, a slot short, reads no premium", older.getFinancingPremium() == 0);

        /* ---- a save from before CBK2, as format 67 wrote it: no rules key, no premium, and the bank's array as CBK1's build
           laid it out - two lines fewer in each of its line blocks, led by the old count, and without CBK2's three slots ---- */
        GameFiles old = copiedTo("centralbankpacecheck-before");
        java.nio.file.Path file = old.saveFile(SLOT);
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(file)).getAsJsonObject();
        Game now = copy();
        double[] withThem = now.getCentralBank().toSaveArray();
        int lines = CentralBank.Line.values().length;
        check("fixture: the save carries the rules, the premium and the bank's array with CBK2's lines and three slots",
                json.has("holdingsRules") && json.getAsJsonArray("expectations").size() == Expectations.SAVE_SLOTS
                        && json.getAsJsonArray("centralBank").size() == withThem.length);
        check("fixture: the bank's array is laid out as read here - its line count where CBK1 put it, its sheets' count after the two line blocks",
                withThem[LINES_AT] == lines && withThem[LINES_AT + 2 * lines + 2] == CentralBank.Sheet.values().length);
        check("fixture: CBK2's two lines are the last of the enum, so a build before it wrote every line before them",
                CentralBank.Line.values()[lines - 2] == CentralBank.Line.BOUGHT_AT_AUCTION && CentralBank.Line.values()[lines - 1] == CentralBank.Line.PAR_AT_AUCTION);
        json.remove("holdingsRules");
        json.getAsJsonArray("expectations").remove(Expectations.SAVE_SLOTS - 1);
        String misread = json.toString();
        com.google.gson.JsonObject wrongWidth = com.google.gson.JsonParser.parseString(misread).getAsJsonObject();
        beforeCbk2(json.getAsJsonArray("centralBank"), lines - CBK2_LINES);
        beforeCbk2(wrongWidth.getAsJsonArray("centralBank"), lines);
        check("fixture: the bank's array is CBK1's width, four lines and three slots short",
                json.getAsJsonArray("centralBank").size() == withThem.length - 2 * CBK2_LINES - 3
                        && json.getAsJsonArray("centralBank").get(LINES_AT).getAsInt() == lines - CBK2_LINES);
        java.nio.file.Files.writeString(file, json.toString());
        Game was = loaded(old);
        HoldingsRules wr = was.getCentralBank().holdingsRules();
        check("a save from before CBK2 reads the rules before it: Auto, runoff and sales, no share, the law off",
                wr.get(HoldingsRules.Dial.PACE) == HoldingsRules.PACE_AUTO && wr.get(HoldingsRules.Dial.QT_MODE) == HoldingsRules.QT_SELL
                        && wr.get(HoldingsRules.Dial.AUCTION_SHARE) == 0 && wr.get(HoldingsRules.Dial.DIRECT_PURCHASES) == HoldingsRules.LAW_OFF);
        check("...a bank that never bought at auction and has lent nothing lately, and no premium",
                was.getCentralBank().getBoughtAtAuctionLifetime() == 0 && was.getCentralBank().getParAtAuctionLifetime() == 0
                        && was.getCentralBank().getFinancedLately() == 0 && was.getExpectations().getFinancingPremium() == 0);
        check("...its bank, every slot of it, the same save's with them to the bit", sameBits(was.getCentralBank().toSaveArray(), withThem));
        check("...and everything else as the same save with them", sameBits(figures(was), figures(now)));
        play(was);
        play(now);
        check("...and it plays its next month to the bit as the same save with them",
                sameBits(figures(was), figures(now)) && sameBits(was.getCentralBank().toSaveArray(), now.getCentralBank().toSaveArray()));

        /* ---- ...and the proof can fail: the same old array read at the new width is not the save with them ---- */
        GameFiles wrong = copiedTo("centralbankpacecheck-misread");
        java.nio.file.Files.writeString(wrong.saveFile(SLOT), wrongWidth.toString());
        Game misreadCity = loaded(wrong);
        check("the old array read as if its line blocks were the new width is another bank, so the proof above would see a loader that misread it",
                !sameBits(misreadCity.getCentralBank().toSaveArray(), withThem));
    }

    /** Where the bank's line count stands in its saved array: thirteen slots, the year of revenue and fourteen more before it (CentralBank.toSaveArray). */
    static final int LINES_AT = 13 + CentralBank.REVENUE_MONTHS + 14;

    /** The lines CBK2 added to CentralBank.Line, the enum's last: BOUGHT_AT_AUCTION and PAR_AT_AUCTION. */
    static final int CBK2_LINES = 2;

    /**
     * The bank's saved array as a build before CBK2 wrote it: CBK2's three trailing slots gone, and its two lines gone from
     * this month's block and from last month's; the blocks led by `count` - the old count for a real old save, the new one
     * for the array a loader that misread the old width would see.
     */
    static void beforeCbk2(com.google.gson.JsonArray bank, int count) {
        int lines = CentralBank.Line.values().length;
        for (int k = 0; k < 3; k++) bank.remove(bank.size() - 1);
        int thisMonth = LINES_AT + 1, lastMonth = thisMonth + lines;
        for (int k = lines - 1; k >= lines - CBK2_LINES; k--) bank.remove(lastMonth + k);
        for (int k = lines - 1; k >= lines - CBK2_LINES; k--) bank.remove(thisMonth + k);
        bank.set(LINES_AT, new com.google.gson.JsonPrimitive(count));
    }
}
